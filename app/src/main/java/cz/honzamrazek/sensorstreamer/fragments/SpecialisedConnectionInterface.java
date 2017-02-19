package cz.honzamrazek.sensorstreamer.fragments;

import cz.honzamrazek.sensorstreamer.models.Connection;

public interface SpecialisedConnectionInterface {
    boolean isValid();
    void commit(Connection connection);
    interface OnFragmentInteractionListener {
        void onValidation(boolean valid);
        Connection onDataLoad();
    }
}
