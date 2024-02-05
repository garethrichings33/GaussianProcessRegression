public class GPRCalculator {
    private double modelCovariance[][];
    private double variance[];
    private double modelWeights[];

    private int numberOfData;
    private int dimensions;
    private boolean modelGenerated;

    private double modelAlpha = 0.5;

    private double modelGammaSquared = 1.0e-8;

    DataTensor data;
    public GPRCalculator(DataTensor data) {
        numberOfData = data.getNumberOfData();
        dimensions = data.getDimensions();
        modelCovariance = new double[numberOfData][numberOfData];
        variance = new double[numberOfData];
        modelWeights = new double[numberOfData];
        modelGenerated = false;
        this.data = data;
    }
    public void createModel(){
        while(modelAlpha < 100) {
            try {
                modelCovariance = createCovariance(modelAlpha, modelGammaSquared);
                Cholesky.cholesky(modelCovariance);
                break;
            }
            catch (IllegalArgumentException ex){
                System.out.println("Problem with matrix shape.");
                return;
            }
            catch (IllegalStateException ex) {
                modelAlpha *= 2;
            }
        }
        if(modelAlpha > 100){
            System.out.println("Cannot create model: Alpha hyperparameter too large.");
            return;
        }

        modelWeights = getWeights(modelCovariance);
        modelGenerated = true;
    }

    public double getPrediction(double[] coordinates){
        if(!modelGenerated)
            throw new IllegalStateException("Cannot get predictions before generating model.");
        double prediction = 0.0;
        for(int i = 0; i < numberOfData; i++)
            prediction += modelWeights[i] * evaluateKernel(coordinates, data.getCoordinates(i), modelAlpha);
        return prediction;
    }
    public double getVariance(double[] coordinates){
        if(!modelGenerated)
            throw new IllegalStateException("Cannot get variance before generating model.");
        double kVector[] = new double[numberOfData];
        for(int i = 0; i < numberOfData; i++)
            kVector[i] = evaluateKernel(coordinates, data.getCoordinates(i), modelAlpha);
        double[] covarianceVector;
        covarianceVector = Cholesky.solveCholesky(modelCovariance, kVector);

        return evaluateKernel(coordinates, coordinates, modelAlpha)
                + modelGammaSquared
                - VectorVectorOperations.dotProduct(kVector, covarianceVector);
    }

    public double getLogMarginalLikelihood(){
        if(!modelGenerated)
            throw new IllegalStateException("Cannot get log marginal likelihood before generating model.");
        return getLogMarginalLikelihood(modelCovariance, modelWeights);
    }

    private double getLogMarginalLikelihood(double covariance[][], double weights[]){
        double logK = covariance[0][0];
        for(int i = 1; i< numberOfData; i++)
            logK += covariance[i][i];
        return -0.5 * (VectorVectorOperations.dotProduct(data.getValueVector(), weights) + Math.log(2.0* Math.PI))
                + logK;
    }

    public void optimiseModel(){
//        double[] alphas = {100, modelAlpha, 0.0};
//        initialBracketing(alphas, modelGammaSquared);
//        System.out.println(alphas[0] + " , " + alphas[1] + " , " + alphas[2]);
        System.out.println(derivativeOfLogMargLike(modelAlpha, modelGammaSquared));
    }

    public void writeWeights(){
        if(!modelGenerated)
            throw new IllegalStateException("Cannot write weights before generating model.");

        for(int i = 0; i<numberOfData; i++)
            System.out.println(i + ": " + modelWeights[i]);
    }

    public double getModelAlpha() {
        return modelAlpha;
    }

    public void setModelAlpha(double modelAlpha) {
        this.modelAlpha = modelAlpha;
    }

    public double getModelGammaSquared() {
        return modelGammaSquared;
    }

    public void setModelGammaSquared(double modelGammaSquared) {
        this.modelGammaSquared = modelGammaSquared;
    }

    private double[][] createCovariance(double alpha, double gammaSquared){
        double[][] covariance = new double[numberOfData][numberOfData];
        for(var i = 0; i < numberOfData; i++)
            for (var j = i; j < numberOfData; j++){
                covariance[i][j] = evaluateKernel(data.getCoordinates(i), data.getCoordinates(j), alpha);
                if(i==j)
                    covariance[i][j] += gammaSquared;
                else
                    covariance[j][i] = covariance[i][j];
            }
        return covariance;
    }

    private double[][] alphaDerivativeOfCovariance(double alpha){
        double[][] derivative = new double[numberOfData][numberOfData];
        for(var i = 0; i < numberOfData; i++)
            for (var j = i; j < numberOfData; j++){
                derivative[i][j] = alphaDerivativeOfKernel(data.getCoordinates(i), data.getCoordinates(j), alpha);
                derivative[j][i] = derivative[i][j];
            }
        return derivative;
    }

    private double updatedLogMarginalLikelihood(double alpha, double gammaSquared){
        double[][] covariance;
        double[] weights;
        covariance = createCovariance(alpha,gammaSquared);
        Cholesky.cholesky(covariance);
        weights = getWeights(covariance);

        return getLogMarginalLikelihood(covariance,weights);
    }

    private double derivativeOfLogMargLike(double alpha, double gammaSquared){
//      Get the necessary components for calculating the derivative:
//      Current covariance matrix and hence its decomposition; the resulting weight vector;
//      the derivative of the covariance matrix wrt the alpha parameter.
        double[][] covariance;
        double[][] covarianceDerivative;
        double[] weights;
        covariance = createCovariance(alpha,gammaSquared);
        Cholesky.cholesky(covariance);
        weights = getWeights(covariance);
        covarianceDerivative = alphaDerivativeOfCovariance(alpha);

//      First term int the derivative is the product of the weight vector and the derivative mateix.
        double derivative =
                VectorVectorOperations.dotProduct(weights, MatrixVectorOperations.matrixVectorProduct(covarianceDerivative, weights));

//      Second term is the trace of the product of the inverse and the derivative of the covariance matrix.
        double[] tempVector = new double[numberOfData];
        double[] resultVector = new double[numberOfData];
        for(int i = 0; i < numberOfData; i++){
            for(int j = 0; j < numberOfData; j++)
                tempVector[j] = covarianceDerivative[j][i];
            resultVector = Cholesky.solveCholesky(covariance, tempVector);
            derivative -= resultVector[i];
        }

//      Finally, all multiplied by a half.
        return 0.5 * derivative;
    }

    private double evaluateKernel(double first[], double second[], double alpha){
        var difference = VectorVectorOperations.difference(first, second);
        return Math.exp(-alpha * VectorVectorOperations.dotProduct(difference, difference));
    }

    private double alphaDerivativeOfKernel(double first[], double second[], double alpha){
        var difference = VectorVectorOperations.difference(first, second);
        var differenceSquared = VectorVectorOperations.dotProduct(difference, difference);
        return -differenceSquared * Math.exp(-alpha * differenceSquared);
    }

    private double[] getWeights(double[][] covariance){
        double[] values = new double[numberOfData];
        for(int i = 0; i < numberOfData; i++)
            values[i] = data.getValue(i);
        return Cholesky.solveCholesky(covariance, values);
    }
}
