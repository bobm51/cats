/* Name: MetaFieldStore.java
 *
 * What;
 *  This file creates a constant Singleton that is the Vector of
 *  FieldInfos for controlling the editing of an AbstractStore FieldInfoStore
 *  (FieldVector).  Some of these values do not matter because they are properties
 *  of the FieldInfo, not of the MetaFieldStore, which controls editing a FieldInfo.
 *  Specifically, the following are not used:
 *  <ul>
 *  <li>Mandatory
 *  <li>Key tag
 *  </ul>
 */
package cats.layout.store;

import cats.gui.store.AlignmentList;
import cats.gui.store.ClassSpec;

/**
 *  This file creates a constant Singleton that is the Vector of
 *  FieldInfos for controlling the editing of an AbstractStore FieldInfoStore
 *  (FieldVector).  Some of these values do not matter because they are properties
 *  of the FieldInfo, not of the MetaFieldStore, which controls editing a FieldInfo.
 *  Specifically, the following are not used:
 *  <ul>
 *  <li>Mandatory
 *  <li>Key tag
 *  </ul>
 * <p>Title: CATS - Computer Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2010</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class MetaFieldStore
    extends FieldVector {
  /**
   * is the Singleton constant.
   */
  public static final MetaFieldStore FieldOfFields = new MetaFieldStore();

  /**
   * is the constructor.
   */
  public MetaFieldStore() {
    add(new FieldInfo(FieldInfo.KEY_TAG, false,
        FieldInfo.KEY_LABEL,
        false, true, FieldInfo.WIDE_WIDTH, AlignmentList.DEFAULT, "",
        String.class));
    add(new FieldInfo(FieldInfo.VISIBLE_TAG, true,
        FieldInfo.VISIBLE_LABEL,
        true, true, FieldInfo.NARROW_WIDTH, AlignmentList.DEFAULT, new Boolean(true),
        Boolean.class));
    add(new FieldInfo(FieldInfo.LABEL_TAG, true,
        FieldInfo.LABEL_LABEL,
        true, true, FieldInfo.WIDE_WIDTH, AlignmentList.DEFAULT, "", String.class));
    add(new FieldInfo(FieldInfo.EDIT_TAG, true,
        FieldInfo.EDIT_LABEL,
        true, true, FieldInfo.NARROW_WIDTH, AlignmentList.DEFAULT, new Boolean(true),
        Boolean.class));
    add(new FieldInfo(FieldInfo.MANDATORY_TAG, false,
        FieldInfo.MANDATORY_LABEL,
        false, true, FieldInfo.MEDIUM_WIDTH, AlignmentList.DEFAULT, new Boolean(false),
        Boolean.class));
    add(new FieldInfo(FieldInfo.WIDTH_TAG, true,
        FieldInfo.WIDTH_LABEL,
        true, true, FieldInfo.NARROW_WIDTH, AlignmentList.DEFAULT, 
        new Integer(FieldInfo.NARROW_WIDTH), Integer.class));
    add(new FieldInfo(FieldInfo.ALIGNMENT_TAG, true,
        FieldInfo.ALIGNMENT_LABEL,
        true, true, FieldInfo.MEDIUM_WIDTH, AlignmentList.DEFAULT, AlignmentList.DEFAULT,
        AlignmentList.class));
    add(new FieldInfo(FieldInfo.DEFAULT_TAG, false,
        FieldInfo.DEFAULT_LABEL,
        true, true, FieldInfo.NARROW_WIDTH, AlignmentList.DEFAULT, "",
        String.class));
    add(new FieldInfo(FieldInfo.CLASS_TAG, true,
        FieldInfo.CLASS_LABEL,
        true, true, FieldInfo.MEDIUM_WIDTH, AlignmentList.DEFAULT, "",
        ClassSpec.class));
//    add(new FieldInfo(FieldInfo.MANDATORY_TAG, true,
//        FieldInfo.MANDATORY_LABEL,
//        true, false, FieldInfo.MEDIUM_WIDTH, new Boolean(false),
//        Boolean.class));
//    add(new FieldInfo(FieldInfo.KEY_TAG, true,
//        FieldInfo.KEY_LABEL,
//        false, true, FieldInfo.WIDE_WIDTH, "",
//        String.class));
//    add(new FieldInfo(FieldInfo.VISIBLE_TAG, true,
//        FieldInfo.VISIBLE_LABEL,
//        true, false, FieldInfo.NARROW_WIDTH, new Boolean(true),
//        Boolean.class));
//    add(new FieldInfo(FieldInfo.LABEL_TAG, true,
//        FieldInfo.LABEL_LABEL,
//        true, false, FieldInfo.WIDE_WIDTH, "", String.class));
//    add(new FieldInfo(FieldInfo.EDIT_TAG, true,
//        FieldInfo.EDIT_LABEL,
//        true, false, FieldInfo.NARROW_WIDTH, new Boolean(true),
//        Boolean.class));
//    add(new FieldInfo(FieldInfo.WIDTH_TAG, true,
//        FieldInfo.WIDTH_LABEL,
//        true, false, FieldInfo.NARROW_WIDTH,
//        new Integer(FieldInfo.NARROW_WIDTH), Integer.class));
//    add(new FieldInfo(FieldInfo.ALIGNMENT_TAG, true,
//        FieldInfo.ALIGNMENT_LABEL,
//        true, false, FieldInfo.MEDIUM_WIDTH, AlignmentList.DEFAULT,
//        AlignmentList.class));
//    add(new FieldInfo(FieldInfo.DEFAULT_TAG, false,
//        FieldInfo.DEFAULT_LABEL,
//        true, false, FieldInfo.NARROW_WIDTH, "",
//        String.class));
//    add(new FieldInfo(FieldInfo.CLASS_TAG, true,
//        FieldInfo.CLASS_LABEL,
//        true, false, FieldInfo.MEDIUM_WIDTH, "",
//        ClassSpec.class));
  }
}
/* @(#)MetaField.java */