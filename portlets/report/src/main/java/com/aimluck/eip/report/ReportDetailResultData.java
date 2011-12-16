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

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 報告書のResultDataです。 <BR>
 * 
 */
public class ReportDetailResultData extends ReportResultData {

  /** ユーザーID */
  private ALNumberField user_id;

  /** メモ */
  private ALStringField note;

  /** 更新日 */
  private ALStringField update_date;

  /**
   *
   *
   */
  @Override
  public void initField() {
    super.initField();
    user_id = new ALNumberField();
    note = new ALStringField();
    note.setTrim(false);
    update_date = new ALStringField();
  }

  /**
   * @return
   */
  public ALNumberField getUserId() {
    return user_id;
  }

  /**
   * @return
   */
  public String getNote() {
    return ALEipUtils.getMessageList(note.getValue());
  }

  /**
   * @param i
   */
  public void setUserId(long i) {
    user_id.setValue(i);
  }

  /**
   * @param string
   */
  public void setNote(String string) {
    note.setValue(string);
  }

  /**
   * @return
   */
  public ALStringField getUpdateDate() {
    return update_date;
  }

  /**
   * @param string
   */
  public void setUpdateDate(String string) {
    update_date.setValue(string);
  }

}