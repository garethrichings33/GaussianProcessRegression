public class VectorVectorOperations {
    static public double dotProduct(double first[], double second[]){
        if(first.length != second.length)
            throw new IllegalStateException("Vectors have different lengths.");
        var product = first[0]*second[0];
        for (var i = 1; i<first.length; i++)
            product += first[i]*second[i];
        return product;
    }

    static public double[] difference(double first[], double second[]){
        if(first.length != second.length)
            throw new IllegalStateException("Vectors have different lengths.");
        double result[] = new double[first.length];
        for(var i = 0; i<result.length; i++)
            result[i] = first[i] - second[i];
        return result;
    }
}
