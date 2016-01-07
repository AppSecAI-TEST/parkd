package com.vinot.parkd;

/**
 * Implementing classes have state-dependence on the Park'd server.
 *
 * For example, if an activity is displaying information about a Location (if parks are
 * occupied, the current parking rate, etc.) then this will have to be checked with the Park'd
 * server or otherwise ascertained in a reliable way.  People's money is depending on this!
 */
public interface ServerRestoreable {
    /**
     * Restore the state of a component, either by via some server interaction or from local
     * storage.
     *
     * @return true if successful, else false
     */
    boolean restoreState();
}
