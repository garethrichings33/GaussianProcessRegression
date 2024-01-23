public class GPR {
    private double modelCovariance[][];
    private double variance[];
    private double modelWeights[];

    private int numberOfData;
    private int dimensions;
    private boolean modelGenerated;

    private double modelAlpha = 0.7;

    private double modelGammaSquared = 1.0e-8;

    DataTensor data;
    public GPR(DataTensor data) {
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

    public double getModelGammaSquared() {
        return modelGammaSquared;
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

//    private void initialBracketing(double[] alphas, double gammaSquared){
//        final double GOLD = 1.618034;
//        final double MINALPHA = 1.0E-2;
//        final double MINIMUM = 1.0E-12;
//
//        double alpha = alphas[0];
//        double beta = alphas[1];
//
//        double lmlAlpha = -updatedLogMarginalLikelihood(alpha, gammaSquared);
//        double lmlBeta = -updatedLogMarginalLikelihood(beta, gammaSquared);
//        double temp;
//        if(lmlBeta < lmlAlpha){
//            temp = lmlAlpha;
//            lmlAlpha = lmlBeta;
//            lmlBeta = temp;
//            temp = alpha;
//            alpha = beta;
//            beta = temp;
//        }
//
//        double step = beta - alpha;
////      Cannot hava non-positive alpha.
//        double delta = Math.max(beta + GOLD*step, MINALPHA);
//        double lmlDelta = -updatedLogMarginalLikelihood(delta, gammaSquared);
//
//        System.out.println(alpha + " , " + lmlAlpha);
//        System.out.println(beta + " , " + lmlBeta);
//        System.out.println(delta + " , " + lmlDelta);
//
//        double rho, sigma, tau;
//        double lmlTau;
//        while(lmlBeta >= lmlDelta){
//            rho = (beta-alpha) * (lmlBeta-lmlDelta);
//            sigma = (beta-delta) * (lmlBeta-lmlAlpha);
//            tau = beta
//                - ((beta-delta)*sigma - (beta-alpha)*rho)
//                / (2.0*Math.signum(sigma-rho) * Math.max(Math.abs(sigma-rho),MINIMUM));
//
//            if((beta - tau)*(tau-delta) > 0.0){
//                lmlTau = -updatedLogMarginalLikelihood(tau, gammaSquared);
//                if(lmlTau < lmlDelta){
//                    alpha = beta;
//                    beta = tau;
//                    alphas = toAlphas(alpha, beta, delta);
//                    return;
//                }
//                else if(lmlTau > lmlBeta){
//                    delta = tau;
//                    alphas = toAlphas(alpha, beta, delta);
//                    return;
//                }
//                tau = delta + GOLD * (delta-beta);
////                lmlTau = updatedLogMarginalLikelihood(tau, gammaSquared);
//            }
//            else if((delta-tau)*(tau - MINALPHA) > 0.0){
//                lmlTau = -updatedLogMarginalLikelihood(tau, gammaSquared);
//                if(lmlTau < lmlDelta) {
//                    beta = delta;
//                    delta = tau;
//                    tau = delta + GOLD*(delta-beta);
//                    lmlBeta = lmlDelta;
//                    lmlDelta = lmlTau;
////                    lmlTau = updatedLogMarginalLikelihood(tau, gammaSquared);
//                }
//            }
//            else if((tau - MINALPHA)*(MINALPHA-delta) >= 0.0){
//                tau = MINALPHA;
////                lmlTau = updatedLogMarginalLikelihood(tau, gammaSquared);
//            }
//            else{
//                tau = delta + GOLD*(delta-beta);
////                lmlTau = updatedLogMarginalLikelihood(tau, gammaSquared);
//            }
//            alpha = beta;
//            beta = delta;
//            delta = tau;
//            lmlAlpha = lmlBeta;
//            lmlBeta = lmlDelta;
//            lmlDelta = -updatedLogMarginalLikelihood(delta, gammaSquared);
//        }
//    }
//    private double[] toAlphas(double a, double b, double c){
//        double[] array = {a,b,c};
//        return array;
//    }

//    private double optimumAlpha(double[] alphas, double gammaSquared){
//        double ax = alphas[0];
//        double bx = alphas[1];
//        double cx = alphas[2];
//        double a = Math.min(ax, cx);
//        double b = a;
//        double v = bx;
//        double w = v;
//        double x = v;
//        double e = 0.0;
//        double fx = updatedLogMarginalLikelihood(x, gammaSquared);
//        double fv = fx;
//        double fw = fx;
//        double dx = derivativeOfLogMargLike(x, gammaSquared);
//        double dv = dx;
//        double dw = dx;
//
//        double xm;
//        double tol = 1.0E-6;
//        double tol1;
//        double tol2;
//        double zeps = 1.0E-10;
//        double d1;
//        double d2;
//        double u1;
//        double u2;
//        boolean ok1;
//        boolean ok2;
//        double olde;
//        double d = 0.0;
//        double u;
//
//        int maxIter = 100;
//        for (int i = 0; i < maxIter; i++) {
//            xm = 0.5*(a+b);
//            tol1 = tol * Math.abs(x) + zeps;
//            tol2 = 2.0 * tol1;
//            if(Math.abs(x-xm) <= (tol2 - 0.5*(b-a)))
//                break;
//            if(Math.abs(e) > tol1){
//                d1 = 2.0 * (b-a);
//                d2 = d1;
//                if(Math.abs(dw-dx) > zeps)
//                    d1 = (w-x) * dx / (dx - dw);
//                if(Math.abs(dv-dx) > zeps)
//                    d2 = (v-x) * dx / (dx - dv);
//                u1 = x + d1;
//                u2 = x + d2;
//                ok1 = ((a-u1)*(u1-b) > 0.0) && (dx*d1 <= 0.0);
//                ok2 = ((a-u2)*(u2-b) > 0.0) && (dx*d2 <= 0.0);
//                olde = e;
//                e = d;
//                if(!ok1 && !ok2){
//                    e = (dx >= 0.0) ? a-x : b-x;
//                    d = 0.5 * e;
//                }
//                else if(ok1 && ok2)
//                    d = (Math.abs(d1) < Math.abs(d2)) ? d1 : d2;
//                else if(ok1)
//                    d = d1;
//                else
//                    d = d2;
//
//                if(Math.abs(d) > Math.abs(0.5 * olde)){
//                    e = (dx >= 0.0) ? a-x : b-x;
//                    d = 0.5 * e;
//                }
//                u = x + d;
//                if((u-a)<tol2 || (b-u) < tol2)
//                    d = ((xm-x) > 0) ? tol1 : -tol1;
//            }
//        }
//
//        return 0;
//    }
}
