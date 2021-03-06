package com.aimluck.eip.cayenne.om.portlet.auto;

import java.util.List;

/** Class _EipMFacility was generated by Cayenne.
  * It is probably a good idea to avoid changing this class manually, 
  * since it may be overwritten next time code is regenerated. 
  * If you need to make any customizations, please use subclass. 
  */
public class _EipMFacility extends org.apache.cayenne.CayenneDataObject {

    public static final String CREATE_DATE_PROPERTY = "createDate";
    public static final String FACILITY_NAME_PROPERTY = "facilityName";
    public static final String NOTE_PROPERTY = "note";
    public static final String SORT_PROPERTY = "sort";
    public static final String UPDATE_DATE_PROPERTY = "updateDate";
    public static final String USER_ID_PROPERTY = "userId";
    public static final String EIP_FACILITY_GROUP_ARRAY_PROPERTY = "eipFacilityGroupArray";
    public static final String EIP_MFACILITY_FACILITY_ID_PROPERTY = "eipMFacilityFacilityId";
    public static final String FROM_EIP_MFACILITY_PROPERTY = "fromEipMFacility";

    public static final String FACILITY_ID_PK_COLUMN = "FACILITY_ID";

    public void setCreateDate(java.util.Date createDate) {
        writeProperty("createDate", createDate);
    }
    public java.util.Date getCreateDate() {
        return (java.util.Date)readProperty("createDate");
    }
    
    
    public void setFacilityName(String facilityName) {
        writeProperty("facilityName", facilityName);
    }
    public String getFacilityName() {
        return (String)readProperty("facilityName");
    }
    
    
    public void setNote(String note) {
        writeProperty("note", note);
    }
    public String getNote() {
        return (String)readProperty("note");
    }
    
    
    public void setSort(Integer sort) {
        writeProperty("sort", sort);
    }
    public Integer getSort() {
        return (Integer)readProperty("sort");
    }
    
    
    public void setUpdateDate(java.util.Date updateDate) {
        writeProperty("updateDate", updateDate);
    }
    public java.util.Date getUpdateDate() {
        return (java.util.Date)readProperty("updateDate");
    }
    
    
    public void setUserId(Integer userId) {
        writeProperty("userId", userId);
    }
    public Integer getUserId() {
        return (Integer)readProperty("userId");
    }
    
    
    public void addToEipFacilityGroupArray(com.aimluck.eip.cayenne.om.portlet.EipFacilityGroup obj) {
        addToManyTarget("eipFacilityGroupArray", obj, true);
    }
    public void removeFromEipFacilityGroupArray(com.aimluck.eip.cayenne.om.portlet.EipFacilityGroup obj) {
        removeToManyTarget("eipFacilityGroupArray", obj, true);
    }
    public List getEipFacilityGroupArray() {
        return (List)readProperty("eipFacilityGroupArray");
    }
    
    
    public void addToEipMFacilityFacilityId(com.aimluck.eip.cayenne.om.portlet.EipMFacilityGroupMap obj) {
        addToManyTarget("eipMFacilityFacilityId", obj, true);
    }
    public void removeFromEipMFacilityFacilityId(com.aimluck.eip.cayenne.om.portlet.EipMFacilityGroupMap obj) {
        removeToManyTarget("eipMFacilityFacilityId", obj, true);
    }
    public List getEipMFacilityFacilityId() {
        return (List)readProperty("eipMFacilityFacilityId");
    }
    
    
    public void addToFromEipMFacility(com.aimluck.eip.cayenne.om.portlet.EipMFacilityGroupMap obj) {
        addToManyTarget("fromEipMFacility", obj, true);
    }
    public void removeFromFromEipMFacility(com.aimluck.eip.cayenne.om.portlet.EipMFacilityGroupMap obj) {
        removeToManyTarget("fromEipMFacility", obj, true);
    }
    public List getFromEipMFacility() {
        return (List)readProperty("fromEipMFacility");
    }
    
    
}
