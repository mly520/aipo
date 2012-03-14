package com.aimluck.eip.cayenne.om.portlet.auto;

import java.util.Date;

import org.apache.cayenne.CayenneDataObject;

import com.aimluck.eip.cayenne.om.portlet.EipTTimeline;

/**
 * Class _EipTTimelineFile was generated by Cayenne. It is probably a good idea
 * to avoid changing this class manually, since it may be overwritten next time
 * code is regenerated. If you need to make any customizations, please use
 * subclass.
 */
public abstract class _EipTTimelineFile extends CayenneDataObject {

  public static final String CREATE_DATE_PROPERTY = "createDate";

  public static final String FILE_NAME_PROPERTY = "fileName";

  public static final String FILE_PATH_PROPERTY = "filePath";

  public static final String FILE_THUMBNAIL_PROPERTY = "fileThumbnail";

  public static final String OWNER_ID_PROPERTY = "ownerId";

  public static final String TIMELINE_ID_PROPERTY = "timelineId";

  public static final String UPDATE_DATE_PROPERTY = "updateDate";

  public static final String EIP_TTIMELINE_PROPERTY = "eipTTimeline";

  public static final String FILE_ID_PK_COLUMN = "FILE_ID";

  public void setCreateDate(Date createDate) {
    writeProperty("createDate", createDate);
  }

  public Date getCreateDate() {
    return (Date) readProperty("createDate");
  }

  public void setFileName(String fileName) {
    writeProperty("fileName", fileName);
  }

  public String getFileName() {
    return (String) readProperty("fileName");
  }

  public void setFilePath(String filePath) {
    writeProperty("filePath", filePath);
  }

  public String getFilePath() {
    return (String) readProperty("filePath");
  }

  public void setFileThumbnail(byte[] fileThumbnail) {
    writeProperty("fileThumbnail", fileThumbnail);
  }

  public byte[] getFileThumbnail() {
    return (byte[]) readProperty("fileThumbnail");
  }

  public void setOwnerId(Integer ownerId) {
    writeProperty("ownerId", ownerId);
  }

  public Integer getOwnerId() {
    return (Integer) readProperty("ownerId");
  }

  public void setTimelineId(Integer timelineId) {
    writeProperty("timelineId", timelineId);
  }

  public Integer getTimelineId() {
    return (Integer) readProperty("timelineId");
  }

  public void setUpdateDate(Date updateDate) {
    writeProperty("updateDate", updateDate);
  }

  public Date getUpdateDate() {
    return (Date) readProperty("updateDate");
  }

  public void setEipTTimeline(EipTTimeline eipTTimeline) {
    setToOneTarget("eipTTimeline", eipTTimeline, true);
  }

  public EipTTimeline getEipTTimeline() {
    return (EipTTimeline) readProperty("eipTTimeline");
  }

}