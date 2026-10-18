package dam.josejuan.u2p4conversor;

import androidx.annotation.StringRes;

/**
 * TODO: Activ 5.1 Una conversión entre dos unidades, válida en los dos sentidos.
 * El factor indica cuántas unidades de destino hay en una unidad de origen.
 */
final class Conversion {

    private final int origen;
    private final int destino;
    private final double factor;

    Conversion(@StringRes int origen, @StringRes int destino, double factor) {
        this.origen = origen;
        this.destino = destino;
        this.factor = factor;
    }

    /** Nombre de la unidad de entrada según el sentido. */
    @StringRes
    int getOrigen(boolean invertido) {
        return invertido ? destino : origen;
    }

    /** Nombre de la unidad de salida según el sentido. */
    @StringRes
    int getDestino(boolean invertido) {
        return invertido ? origen : destino;
    }

    double convertir(double valor, boolean invertido) {
        return invertido ? valor / factor : valor * factor;
    }
}
