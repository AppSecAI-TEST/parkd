package com.vinot.parkd;

/**
 * Implementing classes have state-dependence on the park'd server
 *
 * For example, if an Activity is displaying information about a Location (if parks are
 * occupied, the current parking rate, etc.) then this will have to be checked with the park'd
 * server or otherwise ascertained in a reliable way.  People's money is depending on this!
 */
public interface ServerRestoreable {

    /**
     * Restore the state of a component by via some server interaction.
     */
    void restoreStatefromServer() throws Exception;

    /**
     * Save the state of a component/object to the park'd server.
     */
    void saveStateToServer() throws Exception;
}
