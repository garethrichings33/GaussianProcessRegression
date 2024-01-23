public class WrapDouble {
    public double value;

    public WrapDouble() {
        value = 0.0;
    }
    public WrapDouble(double value) {
        this.value = value;
    }

    public double plus(WrapDouble argument){
        return this.value + argument.value;
    }

    public double minus(WrapDouble argument){
        return this.value - argument.value;
    }

    public boolean gt(WrapDouble argument){
        return this.value > argument.value;
    }

    public boolean lt(WrapDouble argument){
        return this.value < argument.value;
    }

    public boolean le(WrapDouble argument){
        return this.value <= argument.value;
    }
}
