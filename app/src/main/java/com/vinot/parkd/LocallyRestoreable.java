package com.vinot.parkd;

/**
 * Implementing classes have state-dependence on the local data.
 *
 * For example, if an Activity is displaying information about a Location (if parks are
 * occupied, the current parking rate, etc.) then this will have to be retrieved from local storage.
 * People's money is depending on this!
 */
public interface LocallyRestoreable {

    /**
     * Is there something to be restored?  Perhaps those objects which me might draw from storage
     * are in fact null
     * @return whether or not objects we are trying to draw from storage are null
     */
    boolean thereIsSomethingToBeRestored() throws Exception;

    /**
     * Restore the state of a component from local storage.
     */
    void restoreStateFromLocal() throws Exception;

    /**
     * Save the state of a component/object to local storage.
     */
    void saveStateToLocal() throws Exception;

}
