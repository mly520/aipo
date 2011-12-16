/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2011 Aimluck,Inc.
 * http://www.aipo.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aimluck.eip.report;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipTReport;
import com.aimluck.eip.cayenne.om.portlet.EipTReportFile;
import com.aimluck.eip.cayenne.om.portlet.EipTReportMap;
import com.aimluck.eip.cayenne.om.portlet.EipTReportMemberMap;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipPost;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.fileupload.beans.FileuploadLiteBean;
import com.aimluck.eip.fileupload.util.FileuploadUtils;
import com.aimluck.eip.mail.ALAdminMailContext;
import com.aimluck.eip.mail.ALAdminMailMessage;
import com.aimluck.eip.mail.ALMailService;
import com.aimluck.eip.mail.util.ALEipUserAddr;
import com.aimluck.eip.mail.util.ALMailUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.report.util.ReportUtils;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.services.orgutils.ALOrgUtilsService;
import com.aimluck.eip.services.storage.ALStorageService;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 報告書のフォームデータを管理するクラスです。 <BR>
 * 
 */
public class ReportFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ReportFormData.class.getName());

  /** 報告書名 */
  private ALStringField report_name;

  /** メモ */
  private ALStringField note;

  /** 日時 */
  private ALDateTimeField createDate;

  /** 通知先ユーザIDリスト */
  private ALStringField positions;

  /** 社内参加者 */
  private ALStringField members;

  /** 社内参加者一覧 */
  private List<ALEipUser> memberList;

  /** 通知先一覧 */
  private List<ALEipUser> mapList;

  /** ファイルアップロードリスト */
  private List<FileuploadLiteBean> fileuploadList;

  /** 添付フォルダ名 */
  private String folderName = null;

  private String orgId;

  private int uid;

  /** <code>login_user</code> ログインユーザー */
  private ALEipUser login_user;

  /** アクセス権限の機能名 */
  private final String aclPortletFeature = null;

  /**
   * 
   * @param action
   * @param rundata
   * @param context
   * 
   * 
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

    login_user = ALEipUtils.getALEipUser(rundata);

    uid = ALEipUtils.getUserId(rundata);
    orgId = Database.getDomainName();
    folderName = rundata.getParameters().getString("folderName");

    // アクセス権
    /*
     * int view_uid = ReportUtils.getViewId(rundata, context, uid); if (view_uid
     * == uid) { aclPortletFeature =
     * ALAccessControlConstants.POERTLET_FEATURE_REPORT_SELF; } else {
     * aclPortletFeature =
     * ALAccessControlConstants.POERTLET_FEATURE_REPORT_OTHER; }
     */
  }

  /**
   * 各フィールドを初期化します。 <BR>
   * 
   * 
   */
  @Override
  public void initField() {
    // 更新日時
    createDate = new ALDateTimeField(ReportUtils.DATE_TIME_FORMAT);
    createDate.setFieldName("日時");
    // 報告書名
    report_name = new ALStringField();
    report_name.setFieldName("タイトル");
    report_name.setTrim(true);
    report_name.setTrim(true);
    // メモ
    note = new ALStringField();
    note.setFieldName("内容");
    note.setTrim(false);
    // 通知先のリスト
    positions = new ALStringField();
    positions.setFieldName("通知先");
    positions.setTrim(true);
    // 社内参加者のリスト
    members = new ALStringField();
    members.setFieldName("社内参加者");
    members.setTrim(true);
    // ファイルリスト
    fileuploadList = new ArrayList<FileuploadLiteBean>();
    // メンバーリスト
    memberList = new ArrayList<ALEipUser>();
    // 通知先リスト
    mapList = new ArrayList<ALEipUser>();
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean setFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {

    boolean res = super.setFormData(rundata, context, msgList);

    if (res) {
      try {

        // 日時
        if (createDate.toString().equals("")) {
          createDate.setValue(Calendar.getInstance().getTime());
        }
        String memberNames[] = rundata.getParameters().getStrings("members");
        if (memberNames != null && memberNames.length > 0) {
          SelectQuery<TurbineUser> query = Database.query(TurbineUser.class);
          Expression exp =
            ExpressionFactory.inExp(
              TurbineUser.LOGIN_NAME_PROPERTY,
              memberNames);
          query.setQualifier(exp);
          memberList.addAll(ALEipUtils.getUsersFromSelectQuery(query));
        }
        if (memberList.size() == 0) {
          memberList.add(login_user);
        }
        String userNames[] = rundata.getParameters().getStrings("positions");
        if (userNames != null && userNames.length > 0) {
          SelectQuery<TurbineUser> query = Database.query(TurbineUser.class);
          Expression exp =
            ExpressionFactory.inExp(TurbineUser.LOGIN_NAME_PROPERTY, userNames);
          query.setQualifier(exp);
          mapList.addAll(ALEipUtils.getUsersFromSelectQuery(query));
        }
        fileuploadList = ReportUtils.getFileuploadList(rundata);
      } catch (Exception ex) {
        logger.error("Exception", ex);
      }
    }
    return res;
  }

  /**
   * リクエストの各フィールドに対する制約条件を設定します。 <BR>
   * 
   * 
   */
  @Override
  protected void setValidator() {
    // 報告書名の文字数制限
    report_name.setNotNull(true);
    report_name.limitMaxLength(50);
    // メモの文字数制限
    note.setNotNull(true);
    note.limitMaxLength(1000);
    // 日付必須項目
    createDate.setNotNull(true);
  }

  /**
   * リクエストのフォームに入力されたデータの妥当性検証を行います。 <BR>
   * 
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   * 
   */
  @Override
  protected boolean validate(List<String> msgList) {
    // 報告書名
    report_name.validate(msgList);
    // メモ
    note.validate(msgList);
    // 日付
    createDate.validate(msgList);

    // 社内参加者
    if (memberList == null || memberList.size() <= 0) {
      msgList.add("『 <span class='em'>社内参加者</span> 』を指定してください。");
    } else if (!(memberList.get(0) instanceof ALEipUser)) {
      msgList.add("社内参加者のユーザーが全て無効、もしくは削除されています。有効なユーザーが一人以上いる経路を選択してください。");
    }

    // 通知先
    if (mapList == null || mapList.size() <= 0) {
      msgList.add("『 <span class='em'>通知先</span> 』を指定してください。");
    } else if (!(mapList.get(0) instanceof ALEipUser)) {
      msgList.add("通知先のユーザーが全て無効、もしくは削除されています。有効なユーザーが一人以上いる経路を選択してください。");
    }

    return (msgList.size() == 0);

  }

  /**
   * リクエストをデータベースから読み出します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // オブジェクトモデルを取得
      EipTReport report = ReportUtils.getEipTReport(rundata, context);
      if (report == null) {
        return false;
      }

      // 報告書名
      report_name.setValue(report.getReportName());
      // メモ
      note.setValue(report.getNote());
      // 日時
      createDate.setValue(report.getCreateDate());

      List<EipTReportMap> maps = ReportUtils.getEipTReportMap(report);
      EipTReportMap map = null;
      int size = maps.size();
      for (int i = 0; i < size; i++) {
        map = maps.get(i);
        int user_id = map.getUserId().intValue();
        mapList.add(ALEipUtils.getALEipUser(user_id));
      }

      List<EipTReportMemberMap> members =
        ReportUtils.getEipTReportMemberMap(report);
      EipTReportMemberMap member = null;
      int size2 = members.size();
      for (int i = 0; i < size2; i++) {
        member = members.get(i);
        int user_id = member.getUserId().intValue();
        memberList.add(ALEipUtils.getALEipUser(user_id));
      }

      List<EipTReportFile> files = ReportUtils.getEipTReportFile(report);
      int size3 = files.size();
      FileuploadLiteBean filebean = null;
      for (int i = 0; i < size3; i++) {
        EipTReportFile file = files.get(i);
        filebean = new FileuploadLiteBean();
        filebean.initField();
        filebean.setFolderName(ReportUtils.PREFIX_DBFILE
          + Integer.toString(file.getFileId()));
        filebean.setFileName(file.getFileName());
        filebean.setFileId(file.getFileId());
        fileuploadList.add(filebean);
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * リクエストをデータベースから削除します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // オブジェクトモデルを取得
      EipTReport report = ReportUtils.getEipTReport(rundata, context);

      // イベントログ用
      String reqname = report.getReportName();

      // ファイル削除処理
      List<String> fpaths = new ArrayList<String>();
      SelectQuery<EipTReportFile> query = Database.query(EipTReportFile.class);
      query.andQualifier(ExpressionFactory.matchDbExp(
        EipTReportFile.EIP_TREPORT_PROPERTY,
        report.getReportId()));
      List<EipTReportFile> files = query.fetchList();
      if (files != null && files.size() > 0) {
        int fsize = files.size();
        for (int j = 0; j < fsize; j++) {
          fpaths.add((files.get(j)).getFilePath());
        }
      }

      if (fpaths.size() > 0) {
        // ローカルファイルに保存されているファイルを削除する．
        int fsize = fpaths.size();
        for (int i = 0; i < fsize; i++) {
          ALStorageService.deleteFile(ReportUtils.getSaveDirPath(orgId, uid)
            + fpaths.get(i));
        }
      }

      // リクエストを削除
      Database.delete(report);
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        report.getReportId(),
        ALEventlogConstants.PORTLET_TYPE_REPORT,
        reqname);

    } catch (Exception ex) {
      Database.rollback();
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * リクエストをデータベースに格納します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) throws ALDBErrorException {
    EipTReport report = null;
    try {
      Date nowDate = Calendar.getInstance().getTime();

      // 新規オブジェクトモデル
      report = Database.create(EipTReport.class);

      // 報告書名
      report.setReportName(report_name.getValue());
      // ユーザーID
      report
        .setUserId(Integer.valueOf((int) login_user.getUserId().getValue()));
      // メモ
      report.setNote(note.getValue());
      // 作成日
      report.setCreateDate(createDate.getValue());
      // 更新日
      report.setUpdateDate(Calendar.getInstance().getTime());
      // ユーザーID
      report.setTurbineUser(ALEipUtils.getTurbineUser(ALEipUtils
        .getUserId(rundata)));

      // 社内参加者
      for (ALEipUser user : memberList) {
        EipTReportMemberMap map = Database.create(EipTReportMemberMap.class);
        int userid = (int) user.getUserId().getValue();

        map.setEipTReport(report);
        map.setUserId(Integer.valueOf(userid));
      }

      // 通知先
      for (ALEipUser user : mapList) {
        EipTReportMap map = Database.create(EipTReportMap.class);
        int userid = (int) user.getUserId().getValue();

        map.setEipTReport(report);
        map.setUserId(Integer.valueOf(userid));
        // R: 未読 A: 既読
        map.setStatus(ReportUtils.DB_STATUS_UNREAD);
        map.setCreateDate(nowDate);
        map.setUpdateDate(nowDate);
      }

      // 添付ファイルを登録する．
      insertAttachmentFiles(fileuploadList, folderName, uid, report, msgList);

      // リクエストを登録
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        report.getReportId(),
        ALEventlogConstants.PORTLET_TYPE_REPORT,
        report_name.getValue());

      // アクティビティを「あなた宛のお知らせ」に表示させる
      String loginName = login_user.getName().getValue();
      List<String> recipients = new ArrayList<String>();
      for (ALEipUser user : mapList) {
        if (login_user.getUserId().getValue() != user.getUserId().getValue()) {
          recipients.add(user.getName().getValue());
        }
      }
      ReportUtils.createReportActivity(report, loginName, recipients, true);

      // 「更新情報」に表示させる。
      ReportUtils.createNewReportActivity(report, loginName, true);

    } catch (Exception ex) {
      Database.rollback();
      logger.error("Exception", ex);
      return false;
    }
    try {
      // メール送信
      int msgType = ALMailUtils.getSendDestType(ALMailUtils.KEY_MSGTYPE_REPORT);
      if (msgType > 0) {
        // パソコンへメールを送信
        List<ALEipUserAddr> destMemberList =
          ALMailUtils.getALEipUserAddrs(
            mapList,
            ALEipUtils.getUserId(rundata),
            false);
        String subject = "[" + ALOrgUtilsService.getAlias() + "]報告書";
        String orgId = Database.getDomainName();

        List<ALAdminMailMessage> messageList =
          new ArrayList<ALAdminMailMessage>();
        for (ALEipUserAddr destMember : destMemberList) {
          ALAdminMailMessage message = new ALAdminMailMessage(destMember);
          message.setPcSubject(subject);
          message.setCellularSubject(subject);
          message.setPcBody(ReportUtils.createMsgForPc(
            rundata,
            report,
            memberList,
            mapList,
            true));
          message.setCellularBody(ReportUtils.createMsgForCellPhone(
            rundata,
            report,
            memberList,
            mapList,
            true,
            destMember.getUserId()));
          messageList.add(message);
        }

        ALMailService.sendAdminMail(new ALAdminMailContext(orgId, ALEipUtils
          .getUserId(rundata), messageList, ALMailUtils
          .getSendDestType(ALMailUtils.KEY_MSGTYPE_REPORT)));
        // msgList.addAll(errors);

      }
    } catch (Exception ex) {
      msgList.add("メールを送信できませんでした。");
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * データベースに格納されているリクエストを更新します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) {
    EipTReport report = null;
    try {
      // オブジェクトモデルを取得
      report = ReportUtils.getEipTReport(rundata, context);
      if (report == null) {
        return false;
      }

      Date nowDate = Calendar.getInstance().getTime();

      // 報告書名
      report.setReportName(report_name.getValue());
      // ユーザーID
      report
        .setUserId(Integer.valueOf((int) login_user.getUserId().getValue()));
      // メモ
      report.setNote(note.getValue());
      // 作成日
      report.setCreateDate(createDate.getValue());
      // 更新日
      report.setUpdateDate(Calendar.getInstance().getTime());
      // ユーザーID
      report.setTurbineUser(ALEipUtils.getTurbineUser(ALEipUtils
        .getUserId(rundata)));

      // 古いマップデータを削除
      List<EipTReportMap> tmp_map = ReportUtils.getEipTReportMap(report);
      List<EipTReportMemberMap> tmp_member_map =
        ReportUtils.getEipTReportMemberMap(report);
      Database.deleteAll(tmp_map);
      Database.deleteAll(tmp_member_map);

      // 社内参加者
      for (ALEipUser user : memberList) {
        EipTReportMemberMap map = Database.create(EipTReportMemberMap.class);
        int userid = (int) user.getUserId().getValue();

        map.setEipTReport(report);
        map.setUserId(Integer.valueOf(userid));
      }

      // 通知先
      for (ALEipUser user : mapList) {
        EipTReportMap map = Database.create(EipTReportMap.class);
        int userid = (int) user.getUserId().getValue();

        map.setEipTReport(report);
        map.setUserId(Integer.valueOf(userid));
        // R: 未読 A: 既読
        // 更新があった際には常に未読とする
        map.setStatus(ReportUtils.DB_STATUS_UNREAD);
        map.setCreateDate(nowDate);
        map.setUpdateDate(nowDate);
      }

      // サーバーに残すファイルのID
      List<Integer> attIdList = getRequestedHasFileIdList(fileuploadList);
      // 現在選択しているエントリが持っているファイル
      List<EipTReportFile> files = ReportUtils.getEipTReportFile(report);
      if (files != null) {
        int size = files.size();
        for (int i = 0; i < size; i++) {
          EipTReportFile file = files.get(i);
          if (!attIdList.contains(file.getFileId())) {
            // ファイルシステムから削除
            ALStorageService.deleteFile(ReportUtils.getSaveDirPath(orgId, uid)
              + file.getFilePath());

            // DBから削除
            Database.delete(file);

          }
        }
      }

      // 添付ファイルを登録する．
      insertAttachmentFiles(fileuploadList, folderName, uid, report, msgList);

      // リクエストを登録
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        report.getReportId(),
        ALEventlogConstants.PORTLET_TYPE_REPORT,
        report_name.getValue());

      // アクティビティを「あなた宛のお知らせ」に表示させる
      String loginName = login_user.getName().getValue();
      List<String> recipients = new ArrayList<String>();
      for (ALEipUser user : mapList) {
        if (login_user.getUserId().getValue() != user.getUserId().getValue()) {
          recipients.add(user.getName().getValue());
        }
      }
      ReportUtils.createReportActivity(report, loginName, recipients, false);

      // 「更新情報」に表示させる。
      ReportUtils.createNewReportActivity(report, loginName, false);

    } catch (Exception ex) {
      Database.rollback();
      logger.error("Exception", ex);
      return false;
    }
    try {
      // メール送信
      int msgType = ALMailUtils.getSendDestType(ALMailUtils.KEY_MSGTYPE_REPORT);
      if (msgType > 0) {
        // パソコンへメールを送信
        List<ALEipUserAddr> destMemberList =
          ALMailUtils.getALEipUserAddrs(
            mapList,
            ALEipUtils.getUserId(rundata),
            false);
        String subject = "[" + ALOrgUtilsService.getAlias() + "]報告書";
        String orgId = Database.getDomainName();

        List<ALAdminMailMessage> messageList =
          new ArrayList<ALAdminMailMessage>();
        for (ALEipUserAddr destMember : destMemberList) {
          ALAdminMailMessage message = new ALAdminMailMessage(destMember);
          message.setPcSubject(subject);
          message.setCellularSubject(subject);
          message.setPcBody(ReportUtils.createMsgForPc(
            rundata,
            report,
            memberList,
            mapList,
            false));
          message.setCellularBody(ReportUtils.createMsgForCellPhone(
            rundata,
            report,
            memberList,
            mapList,
            false,
            destMember.getUserId()));
          messageList.add(message);
        }

        ALMailService.sendAdminMail(new ALAdminMailContext(orgId, ALEipUtils
          .getUserId(rundata), messageList, ALMailUtils
          .getSendDestType(ALMailUtils.KEY_MSGTYPE_REPORT)));
        // msgList.addAll(errors);

      }
    } catch (Exception ex) {
      msgList.add("メールを送信できませんでした。");
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  private List<Integer> getRequestedHasFileIdList(
      List<FileuploadLiteBean> attachmentFileNameList) {
    List<Integer> idlist = new ArrayList<Integer>();
    FileuploadLiteBean filebean = null;
    if (attachmentFileNameList != null) {
      int size = attachmentFileNameList.size();
      for (int i = 0; i < size; i++) {
        filebean = attachmentFileNameList.get(i);
        if (!filebean.isNewFile()) {
          int index = filebean.getFileId();
          idlist.add(Integer.valueOf(index));
        }
      }
    }
    return idlist;
  }

  private boolean insertAttachmentFiles(
      List<FileuploadLiteBean> fileuploadList, String folderName, int uid,
      EipTReport entry, List<String> msgList) {

    if (fileuploadList == null || fileuploadList.size() <= 0) {
      return true;
    }

    try {
      int length = fileuploadList.size();
      ArrayList<FileuploadLiteBean> newfilebeans =
        new ArrayList<FileuploadLiteBean>();
      FileuploadLiteBean filebean = null;
      for (int i = 0; i < length; i++) {
        filebean = fileuploadList.get(i);
        if (filebean.isNewFile()) {
          newfilebeans.add(filebean);
        }
      }
      int newfilebeansSize = newfilebeans.size();
      if (newfilebeansSize > 0) {
        FileuploadLiteBean newfilebean = null;
        for (int j = 0; j < length; j++) {
          newfilebean = newfilebeans.get(j);
          // サムネイル処理
          String[] acceptExts = ImageIO.getWriterFormatNames();
          byte[] fileThumbnail =
            FileuploadUtils.getBytesShrinkFilebean(
              orgId,
              folderName,
              uid,
              newfilebean,
              acceptExts,
              FileuploadUtils.DEF_THUMBNAIL_WIDTH,
              FileuploadUtils.DEF_THUMBNAIL_HEIGTH,
              msgList);

          String filename = j + "_" + String.valueOf(System.nanoTime());

          // 新規オブジェクトモデル
          EipTReportFile file = Database.create(EipTReportFile.class);
          file.setOwnerId(Integer.valueOf(uid));
          file.setFileName(newfilebean.getFileName());
          file.setFilePath(ReportUtils.getRelativePath(filename));
          if (fileThumbnail != null) {
            file.setFileThumbnail(fileThumbnail);
          }
          file.setEipTReport(entry);
          file.setCreateDate(Calendar.getInstance().getTime());
          file.setUpdateDate(Calendar.getInstance().getTime());

          // ファイルの移動
          ALStorageService.copyTmpFile(
            uid,
            folderName,
            String.valueOf(newfilebean.getFileId()),
            ReportUtils.FOLDER_FILEDIR_REPORT,
            ReportUtils.CATEGORY_KEY + ALStorageService.separator() + uid,
            filename);
        }

        // 添付ファイル保存先のフォルダを削除
        ALStorageService.deleteTmpFolder(uid, folderName);
      }

    } catch (Exception e) {
      logger.error(e);
    }
    return true;
  }

  /**
   * メモを取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getNote() {
    return note;
  }

  /**
   * メモのフィールドを設定します。 <BR>
   * 
   * @param str
   * @return
   */
  public void setNote(String str) {
    note.setValue(str);
  }

  /**
   * 報告書名を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getReportName() {
    return report_name;
  }

  /**
   * 報告書名を格納します。 <BR>
   * 
   * @param str
   * @return
   */

  public void setReportName(String str) {
    report_name.setValue(str);
  }

  /**
   * @return
   */
  public ALDateTimeField getCreateDate() {
    return createDate;
  }

  /**
   * グループメンバーを取得します。 <BR>
   * 
   * @return
   */
  public List<ALEipUser> getMemberList() {
    return memberList;
  }

  /**
   * グループメンバーを格納します。 <BR>
   * 
   * @param str
   * @return
   */
  public void setMemberList(ArrayList<ALEipUser> list) {
    memberList = list;
  }

  /**
   * グループメンバーを取得します。 <BR>
   * 
   * @return
   */
  public List<ALEipUser> getMapList() {
    return mapList;
  }

  /**
   * グループメンバーを格納します。 <BR>
   * 
   * @param str
   * @return
   */
  public void setMapList(ArrayList<ALEipUser> list) {
    mapList = list;
  }

  /**
   * 
   * @param groupname
   * @return
   */
  public List<ALEipUser> getUsers(String groupname) {
    return ALEipUtils.getUsers(groupname);
  }

  /**
   * 
   * @return
   */
  public Map<Integer, ALEipPost> getPostMap() {
    return ALEipManager.getInstance().getPostMap();
  }

  public List<FileuploadLiteBean> getAttachmentFileNameList() {
    return fileuploadList;
  }

  /**
   * アクセス権限チェック用メソッド。<br />
   * アクセス権限の機能名を返します。
   * 
   * @return
   */
  @Override
  public String getAclPortletFeature() {
    return aclPortletFeature;
  }
}
