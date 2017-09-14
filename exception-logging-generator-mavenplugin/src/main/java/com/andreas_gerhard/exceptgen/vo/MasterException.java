package com.andreas_gerhard.exceptgen.vo;

/**
 * Generated in Project exception-logging-generator for
 *
 * @author angerhar
 * @version %$LastChangedRevision:  $version:  %
 * @noa.created_by %$LastChangedBy: angerhar $created_by: angerhar %
 * @noa.date_created %$LastChangedDate:  %
 */
public class MasterException {


    private String masterPackageName;
    private String masterClassName;
    private String masterInheritClassName;

    public String getMasterPackageName() {
        return masterPackageName;
    }

    public void setMasterPackageName(String masterPackageName) {
        this.masterPackageName = masterPackageName;
    }

    public String getMasterClassName() {
        return masterClassName;
    }

    public void setMasterClassName(String masterClassName) {
        this.masterClassName = masterClassName;
    }

    public String getMasterInheritClassName() {
        return masterInheritClassName;
    }

    public void setMasterInheritClassName(String masterInheritClassName) {
        this.masterInheritClassName = masterInheritClassName;
    }

}
