package life.qbic.portal.portlet.logging

/**
 * <class short description - One Line!>
 *
 * <More detailed description - When to use, what it solves, etc.>
 *
 * @since: <versiontag>
 *
 */
class LogFactory {

    /**
     * This methods creates a Logger and returns it for usage
     *
     * @param clazz which for which a logger should be created
     * @return Log to handle logging
     */
    static Log createLog(Class clazz){
        new Log(clazz)
    }

}
