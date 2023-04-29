package nurimsoft.stat.pivot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class QueryResult{
	
	protected Log log = LogFactory.getLog(this.getClass());

	Map<List<String>, String> resultMap = new HashMap<List<String>, String>();
	
	Map<String, String> paramMap;	//parameter ::: orgId, tblId..등
	List<Dimension> classDimList;
	
	int dimensionCount;	//Dimension 수
	
	public Map getResultMap(){
		return resultMap;
	}
	
	public String generateQuery(){
		//TO-DO generateQuery
		//paramMap을 이용하여...sql을 동적으로 생성한다.
		//많은 로직이 포함될 예정이므로 나름..구조화하여야 한다.
		
		StringBuffer strBuff = new StringBuffer();
		//strBuff.append(" select * from (														    \n");
		strBuff.append(" SELECT  /*+  use_hash(a,b)*/														    \n");
		strBuff.append("                         --A.ORG_ID AS ORG_ID,														    \n");
		strBuff.append("                         --A.TBL_ID AS TBL_ID,														    \n");
		strBuff.append("                         A.ITM_RCGN_SN,														    \n");
		strBuff.append("                         B.PRD_SE AS PRD_SE,														    \n");
		strBuff.append("                         B.PRD_DE AS PRD_DE,														    \n");
		strBuff.append("                         CASE WHEN A.OV_L1_CO > 1														    \n");
		strBuff.append("                                 THEN (														    \n");
		strBuff.append("                                         SELECT  UP_ITM_ID FROM TN_ITM_LIST S													    \n");
		strBuff.append("                                         WHERE   ORG_ID = A.ORG_ID														    \n");
		strBuff.append("                                                 AND TBL_ID = A.TBL_ID														    \n");
		strBuff.append("                                                 AND ITM_ID = A.OV_L1_ID													    \n");
		strBuff.append("                                                 AND OBJ_VAR_ID = (														    \n");
		strBuff.append("                                                                     SELECT  OBJ_VAR_ID												    \n");
		strBuff.append("                                                                     FROM    TN_OBJ_ITM_CLS											    \n");
		strBuff.append("                                                                     WHERE   ORG_ID = S.ORG_ID											    \n");
		strBuff.append("                                                                             AND TBL_ID = S.TBL_ID										    \n");
		strBuff.append("                                                                             AND VAR_ORD_SN = 1											    \n");
		strBuff.append("                                                                  )														    \n");
		strBuff.append("                                      )														    \n");
		strBuff.append("                              ELSE NULL														    \n");
		strBuff.append("                         END AS OV_L1_UP_ID,														    \n");
		strBuff.append("                         A.OV_L1_ID AS OV_L1_ID,														    \n");
		strBuff.append("                         (SELECT SCR_KOR FROM TN_ITM_LIST WHERE org_id = a.org_id and tbl_id = a.tbl_id and ITM_ID = OV_L1_ID  AND OBJ_VAR_ID = (				    \n");
		strBuff.append("                                                                     SELECT  OBJ_VAR_ID												    \n");
		strBuff.append("                                                                     FROM    TN_OBJ_ITM_CLS											    \n");
		strBuff.append("                                                                     WHERE   ORG_ID = a.ORG_ID											    \n");
		strBuff.append("                                                                             AND TBL_ID = a.TBL_ID										    \n");
		strBuff.append("                                                                             AND VAR_ORD_SN = 1											    \n");
		strBuff.append("                                                                  )) AS OV_L1_NM,												    \n");
		strBuff.append("                          (SELECT  CHAR_ITM_SN FROM TN_ITM_LIST WHERE org_id = a.org_id and tbl_id = a.tbl_id and ITM_ID = OV_L1_ID  AND OBJ_VAR_ID = (				    \n");
		strBuff.append("                                                                     SELECT  OBJ_VAR_ID												    \n");
		strBuff.append("                                                                     FROM    TN_OBJ_ITM_CLS											    \n");
		strBuff.append("                                                                     WHERE   ORG_ID = a.ORG_ID											    \n");
		strBuff.append("                                                                             AND TBL_ID = a.TBL_ID										    \n");
		strBuff.append("                                                                             AND VAR_ORD_SN = 1											    \n");
		strBuff.append("                                                                  )) AS OV_L1_SN,												    \n");
		strBuff.append("                         CASE WHEN A.OV_L2_CO > 1														    \n");
		strBuff.append("                                 THEN (														    \n");
		strBuff.append("                                         SELECT  UP_ITM_ID FROM TN_ITM_LIST S													    \n");
		strBuff.append("                                         WHERE   ORG_ID = A.ORG_ID														    \n");
		strBuff.append("                                                 AND TBL_ID = A.TBL_ID														    \n");
		strBuff.append("                                                 AND ITM_ID = A.OV_L2_ID													    \n");
		strBuff.append("                                                 AND OBJ_VAR_ID = (														    \n");
		strBuff.append("                                                                     SELECT  OBJ_VAR_ID												    \n");
		strBuff.append("                                                                     FROM    TN_OBJ_ITM_CLS											    \n");
		strBuff.append("                                                                     WHERE   ORG_ID = S.ORG_ID											    \n");
		strBuff.append("                                                                             AND TBL_ID = S.TBL_ID										    \n");
		strBuff.append("                                                                             AND VAR_ORD_SN = 2											    \n");
		strBuff.append("                                                                  )														    \n");
		strBuff.append("                                      )														    \n");
		strBuff.append("                              ELSE NULL														    \n");
		strBuff.append("                         END AS OV_L2_UP_ID,														    \n");
		strBuff.append("                         A.OV_L2_ID AS OV_L2_ID,														    \n");
		strBuff.append("                         (SELECT SCR_KOR FROM TN_ITM_LIST WHERE org_id = a.org_id and tbl_id = a.tbl_id and ITM_ID = OV_L2_ID AND OBJ_VAR_ID = (				    \n");
		strBuff.append("                                                                     SELECT  OBJ_VAR_ID												    \n");
		strBuff.append("                                                                     FROM    TN_OBJ_ITM_CLS											    \n");
		strBuff.append("                                                                     WHERE   ORG_ID = a.ORG_ID											    \n");
		strBuff.append("                                                                             AND TBL_ID = a.TBL_ID										    \n");
		strBuff.append("                                                                             AND VAR_ORD_SN = 2											    \n");
		strBuff.append("                                                                  )) AS OV_L2_NM,												    \n");
		strBuff.append("                         (SELECT  CHAR_ITM_SN FROM TN_ITM_LIST WHERE org_id = a.org_id and tbl_id = a.tbl_id and ITM_ID = OV_L2_ID  AND OBJ_VAR_ID = (				    \n");
		strBuff.append("                                                                     SELECT  OBJ_VAR_ID												    \n");
		strBuff.append("                                                                     FROM    TN_OBJ_ITM_CLS											    \n");
		strBuff.append("                                                                     WHERE   ORG_ID = a.ORG_ID											    \n");
		strBuff.append("                                                                             AND TBL_ID = a.TBL_ID										    \n");
		strBuff.append("                                                                             AND VAR_ORD_SN = 2											    \n");
		strBuff.append("                                                                  )) AS OV_L2_SN,												    \n");
		strBuff.append("                         CASE WHEN A.OV_L3_CO > 1														    \n");
		strBuff.append("                                 THEN (														    \n");
		strBuff.append("                                         SELECT  UP_ITM_ID FROM TN_ITM_LIST S													    \n");
		strBuff.append("                                         WHERE   ORG_ID = A.ORG_ID														    \n");
		strBuff.append("                                                 AND TBL_ID = A.TBL_ID														    \n");
		strBuff.append("                                                 AND ITM_ID = A.OV_L3_ID													    \n");
		strBuff.append("                                                 AND OBJ_VAR_ID = (														    \n");
		strBuff.append("                                                                     SELECT  OBJ_VAR_ID												    \n");
		strBuff.append("                                                                     FROM    TN_OBJ_ITM_CLS											    \n");
		strBuff.append("                                                                     WHERE   ORG_ID = S.ORG_ID											    \n");
		strBuff.append("                                                                             AND TBL_ID = S.TBL_ID										    \n");
		strBuff.append("                                                                             AND VAR_ORD_SN = 3											    \n");
		strBuff.append("                                                                  )														    \n");
		strBuff.append("                                      )														    \n");
		strBuff.append("                              ELSE NULL														    \n");
		strBuff.append("                         END AS OV_L3_UP_ID,														    \n");
		strBuff.append("                         A.OV_L3_ID AS OV_L3_ID,														    \n");
		strBuff.append("                         (SELECT SCR_KOR FROM TN_ITM_LIST WHERE org_id = a.org_id and tbl_id = a.tbl_id and ITM_ID = OV_L3_ID  AND OBJ_VAR_ID = (				    \n");
		strBuff.append("                                                                     SELECT  OBJ_VAR_ID												    \n");
		strBuff.append("                                                                     FROM    TN_OBJ_ITM_CLS											    \n");
		strBuff.append("                                                                     WHERE   ORG_ID = a.ORG_ID											    \n");
		strBuff.append("                                                                             AND TBL_ID = a.TBL_ID										    \n");
		strBuff.append("                                                                             AND VAR_ORD_SN = 3											    \n");
		strBuff.append("                                                                  )) AS OV_L3_NM,												    \n");
		strBuff.append("                          (SELECT  CHAR_ITM_SN FROM TN_ITM_LIST WHERE org_id = a.org_id and tbl_id = a.tbl_id and ITM_ID = OV_L3_ID  AND OBJ_VAR_ID = (				    \n");
		strBuff.append("                                                                     SELECT  OBJ_VAR_ID												    \n");
		strBuff.append("                                                                     FROM    TN_OBJ_ITM_CLS											    \n");
		strBuff.append("                                                                     WHERE   ORG_ID = a.ORG_ID											    \n");
		strBuff.append("                                                                             AND TBL_ID = a.TBL_ID										    \n");
		strBuff.append("                                                                             AND VAR_ORD_SN = 3											    \n");
		strBuff.append("                                                                  )) AS OV_L3_SN,												    \n");
		strBuff.append("                         A.CHAR_ITM_ID AS CHAR_ITM_ID,														    \n");
		strBuff.append("                         (SELECT SCR_KOR FROM TN_ITM_LIST WHERE org_id = a.org_id and tbl_id = a.tbl_id and obj_var_id = '13999001' and ITM_ID = CHAR_ITM_ID) AS CHAR_ITM_NM,	    \n");
		strBuff.append("                         (SELECT CHAR_ITM_SN FROM TN_ITM_LIST WHERE org_id = a.org_id and tbl_id = a.tbl_id and obj_var_id = '13999001' and ITM_ID = CHAR_ITM_ID) AS CHAR_ITM_SN,   \n");
		strBuff.append("                         'test' as CHAR_UNIT_ID,														    \n");
		strBuff.append("                         B.DTVAL_CO AS DTVAL_CO,														    \n");
		strBuff.append("                         9 AS WGT_CO,														    \n");
		strBuff.append("                         A.UNIT_ID														    \n");
		strBuff.append("                 FROM    TN_DIM A, TN_DT B														    \n");
		strBuff.append("                 WHERE   A.ORG_ID = B.ORG_ID														    \n");
		strBuff.append("                         AND A.TBL_ID = B.TBL_ID														    \n");
		strBuff.append("                         AND A.ITM_RCGN_SN = B.ITM_RCGN_SN														    \n");
		strBuff.append("                         AND A.ORG_ID = '301'														    \n");
		strBuff.append("                         AND A.TBL_ID = 'DT_027Y118'														    \n");
		strBuff.append("                         AND B.PRD_SE = 'Y'														    \n");
		strBuff.append("                         and b.prd_de in ('1998', '1999')														    \n");
		strBuff.append("                         and a.ov_l1_id in (SELECT itm_id FROM TN_ITM_LIST WHERE org_id = a.org_id and tbl_id = a.tbl_id AND OBJ_VAR_ID = (					    \n");
		strBuff.append("                                                                     SELECT  OBJ_VAR_ID												    \n");
		strBuff.append("                                                                     FROM    TN_OBJ_ITM_CLS											    \n");
		strBuff.append("                                                                     WHERE   ORG_ID = a.ORG_ID											    \n");
		strBuff.append("                                                                             AND TBL_ID = a.TBL_ID										    \n");
		strBuff.append("                                                                             AND VAR_ORD_SN = 1											    \n");
		strBuff.append("                                                                  ))														    \n");
		strBuff.append("                         and a.ov_l2_id in (SELECT itm_id FROM TN_ITM_LIST WHERE org_id = a.org_id and tbl_id = a.tbl_id AND OBJ_VAR_ID = (					    \n");
		strBuff.append("                                                                     SELECT  OBJ_VAR_ID												    \n");
		strBuff.append("                                                                     FROM    TN_OBJ_ITM_CLS											    \n");
		strBuff.append("                                                                     WHERE   ORG_ID = a.ORG_ID											    \n");
		strBuff.append("                                                                             AND TBL_ID = a.TBL_ID										    \n");
		strBuff.append("                                                                             AND VAR_ORD_SN = 2											    \n");
		strBuff.append("                                                                  ))														    \n");
		strBuff.append("                         and a.ov_l3_id in (SELECT itm_id FROM TN_ITM_LIST WHERE org_id = a.org_id and tbl_id = a.tbl_id AND OBJ_VAR_ID = (					    \n");
		strBuff.append("                                                                     SELECT  OBJ_VAR_ID												    \n");
		strBuff.append("                                                                     FROM    TN_OBJ_ITM_CLS											    \n");
		strBuff.append("                                                                     WHERE   ORG_ID = a.ORG_ID											    \n");
		strBuff.append("                                                                             AND TBL_ID = a.TBL_ID										    \n");
		strBuff.append("                                                                             AND VAR_ORD_SN = 3											    \n");
		strBuff.append("                                                                  ))														    \n");
		strBuff.append("                        order by ov_l1_sn, ov_l1_id, ov_l2_sn, ov_l2_id, ov_l3_sn, ov_l3_id, char_itm_sn, char_itm_id								    \n");
		//strBuff.append("                        ) where rownum <= 100								    \n");
		
		return strBuff.toString();
		
	}
	
	public String generateQuery2(){
		//TO-DO generateQuery
		//paramMap을 이용하여...sql을 동적으로 생성한다.
		//많은 로직이 포함될 예정이므로 나름..구조화하여야 한다.
		
		StringBuffer strBuff = new StringBuffer();
		//strBuff.append(" select * from (														    \n");
		strBuff.append(" SELECT  /*+  use_hash(a,b)*/														    \n");
		strBuff.append("                         --A.ORG_ID AS ORG_ID,														    \n");
		strBuff.append("                         --A.TBL_ID AS TBL_ID,														    \n");
		strBuff.append("                         A.ITM_RCGN_SN,														    \n");
		strBuff.append("                         B.PRD_SE AS PRD_SE,														    \n");
		strBuff.append("                         B.PRD_DE AS PRD_DE,														    \n");
		strBuff.append("                         CASE WHEN A.OV_L1_CO > 1														    \n");
		strBuff.append("                                 THEN (														    \n");
		strBuff.append("                                         SELECT  UP_ITM_ID FROM TN_ITM_LIST S													    \n");
		strBuff.append("                                         WHERE   ORG_ID = A.ORG_ID														    \n");
		strBuff.append("                                                 AND TBL_ID = A.TBL_ID														    \n");
		strBuff.append("                                                 AND ITM_ID = A.OV_L1_ID													    \n");
		strBuff.append("                                                 AND OBJ_VAR_ID = (														    \n");
		strBuff.append("                                                                     SELECT  OBJ_VAR_ID												    \n");
		strBuff.append("                                                                     FROM    TN_OBJ_ITM_CLS											    \n");
		strBuff.append("                                                                     WHERE   ORG_ID = S.ORG_ID											    \n");
		strBuff.append("                                                                             AND TBL_ID = S.TBL_ID										    \n");
		strBuff.append("                                                                             AND VAR_ORD_SN = 1											    \n");
		strBuff.append("                                                                  )														    \n");
		strBuff.append("                                      )														    \n");
		strBuff.append("                              ELSE NULL														    \n");
		strBuff.append("                         END AS OV_L1_UP_ID,														    \n");
		strBuff.append("                         A.OV_L1_ID AS OV_L1_ID,														    \n");
		strBuff.append("                         (SELECT SCR_KOR FROM TN_ITM_LIST WHERE org_id = a.org_id and tbl_id = a.tbl_id and ITM_ID = OV_L1_ID  AND OBJ_VAR_ID = (				    \n");
		strBuff.append("                                                                     SELECT  OBJ_VAR_ID												    \n");
		strBuff.append("                                                                     FROM    TN_OBJ_ITM_CLS											    \n");
		strBuff.append("                                                                     WHERE   ORG_ID = a.ORG_ID											    \n");
		strBuff.append("                                                                             AND TBL_ID = a.TBL_ID										    \n");
		strBuff.append("                                                                             AND VAR_ORD_SN = 1											    \n");
		strBuff.append("                                                                  )) AS OV_L1_NM,												    \n");
		strBuff.append("                          (SELECT  CHAR_ITM_SN FROM TN_ITM_LIST WHERE org_id = a.org_id and tbl_id = a.tbl_id and ITM_ID = OV_L1_ID  AND OBJ_VAR_ID = (				    \n");
		strBuff.append("                                                                     SELECT  OBJ_VAR_ID												    \n");
		strBuff.append("                                                                     FROM    TN_OBJ_ITM_CLS											    \n");
		strBuff.append("                                                                     WHERE   ORG_ID = a.ORG_ID											    \n");
		strBuff.append("                                                                             AND TBL_ID = a.TBL_ID										    \n");
		strBuff.append("                                                                             AND VAR_ORD_SN = 1											    \n");
		strBuff.append("                                                                  )) AS OV_L1_SN,												    \n");
		strBuff.append("                         CASE WHEN A.OV_L2_CO > 1														    \n");
		strBuff.append("                                 THEN (														    \n");
		strBuff.append("                                         SELECT  UP_ITM_ID FROM TN_ITM_LIST S													    \n");
		strBuff.append("                                         WHERE   ORG_ID = A.ORG_ID														    \n");
		strBuff.append("                                                 AND TBL_ID = A.TBL_ID														    \n");
		strBuff.append("                                                 AND ITM_ID = A.OV_L2_ID													    \n");
		strBuff.append("                                                 AND OBJ_VAR_ID = (														    \n");
		strBuff.append("                                                                     SELECT  OBJ_VAR_ID												    \n");
		strBuff.append("                                                                     FROM    TN_OBJ_ITM_CLS											    \n");
		strBuff.append("                                                                     WHERE   ORG_ID = S.ORG_ID											    \n");
		strBuff.append("                                                                             AND TBL_ID = S.TBL_ID										    \n");
		strBuff.append("                                                                             AND VAR_ORD_SN = 2											    \n");
		strBuff.append("                                                                  )														    \n");
		strBuff.append("                                      )														    \n");
		strBuff.append("                              ELSE NULL														    \n");
		strBuff.append("                         END AS OV_L2_UP_ID,														    \n");
		strBuff.append("                         A.OV_L2_ID AS OV_L2_ID,														    \n");
		strBuff.append("                         (SELECT SCR_KOR FROM TN_ITM_LIST WHERE org_id = a.org_id and tbl_id = a.tbl_id and ITM_ID = OV_L2_ID AND OBJ_VAR_ID = (				    \n");
		strBuff.append("                                                                     SELECT  OBJ_VAR_ID												    \n");
		strBuff.append("                                                                     FROM    TN_OBJ_ITM_CLS											    \n");
		strBuff.append("                                                                     WHERE   ORG_ID = a.ORG_ID											    \n");
		strBuff.append("                                                                             AND TBL_ID = a.TBL_ID										    \n");
		strBuff.append("                                                                             AND VAR_ORD_SN = 2											    \n");
		strBuff.append("                                                                  )) AS OV_L2_NM,												    \n");
		strBuff.append("                         (SELECT  CHAR_ITM_SN FROM TN_ITM_LIST WHERE org_id = a.org_id and tbl_id = a.tbl_id and ITM_ID = OV_L2_ID  AND OBJ_VAR_ID = (				    \n");
		strBuff.append("                                                                     SELECT  OBJ_VAR_ID												    \n");
		strBuff.append("                                                                     FROM    TN_OBJ_ITM_CLS											    \n");
		strBuff.append("                                                                     WHERE   ORG_ID = a.ORG_ID											    \n");
		strBuff.append("                                                                             AND TBL_ID = a.TBL_ID										    \n");
		strBuff.append("                                                                             AND VAR_ORD_SN = 2											    \n");
		strBuff.append("                                                                  )) AS OV_L2_SN,												    \n");
		strBuff.append("                         CASE WHEN A.OV_L3_CO > 1														    \n");
		strBuff.append("                                 THEN (														    \n");
		strBuff.append("                                         SELECT  UP_ITM_ID FROM TN_ITM_LIST S													    \n");
		strBuff.append("                                         WHERE   ORG_ID = A.ORG_ID														    \n");
		strBuff.append("                                                 AND TBL_ID = A.TBL_ID														    \n");
		strBuff.append("                                                 AND ITM_ID = A.OV_L3_ID													    \n");
		strBuff.append("                                                 AND OBJ_VAR_ID = (														    \n");
		strBuff.append("                                                                     SELECT  OBJ_VAR_ID												    \n");
		strBuff.append("                                                                     FROM    TN_OBJ_ITM_CLS											    \n");
		strBuff.append("                                                                     WHERE   ORG_ID = S.ORG_ID											    \n");
		strBuff.append("                                                                             AND TBL_ID = S.TBL_ID										    \n");
		strBuff.append("                                                                             AND VAR_ORD_SN = 3											    \n");
		strBuff.append("                                                                  )														    \n");
		strBuff.append("                                      )														    \n");
		strBuff.append("                              ELSE NULL														    \n");
		strBuff.append("                         END AS OV_L3_UP_ID,														    \n");
		strBuff.append("                         A.OV_L3_ID AS OV_L3_ID,														    \n");
		strBuff.append("                         (SELECT SCR_KOR FROM TN_ITM_LIST WHERE org_id = a.org_id and tbl_id = a.tbl_id and ITM_ID = OV_L3_ID  AND OBJ_VAR_ID = (				    \n");
		strBuff.append("                                                                     SELECT  OBJ_VAR_ID												    \n");
		strBuff.append("                                                                     FROM    TN_OBJ_ITM_CLS											    \n");
		strBuff.append("                                                                     WHERE   ORG_ID = a.ORG_ID											    \n");
		strBuff.append("                                                                             AND TBL_ID = a.TBL_ID										    \n");
		strBuff.append("                                                                             AND VAR_ORD_SN = 3											    \n");
		strBuff.append("                                                                  )) AS OV_L3_NM,												    \n");
		strBuff.append("                          (SELECT  CHAR_ITM_SN FROM TN_ITM_LIST WHERE org_id = a.org_id and tbl_id = a.tbl_id and ITM_ID = OV_L3_ID  AND OBJ_VAR_ID = (				    \n");
		strBuff.append("                                                                     SELECT  OBJ_VAR_ID												    \n");
		strBuff.append("                                                                     FROM    TN_OBJ_ITM_CLS											    \n");
		strBuff.append("                                                                     WHERE   ORG_ID = a.ORG_ID											    \n");
		strBuff.append("                                                                             AND TBL_ID = a.TBL_ID										    \n");
		strBuff.append("                                                                             AND VAR_ORD_SN = 3											    \n");
		strBuff.append("                                                                  )) AS OV_L3_SN,												    \n");
		strBuff.append("                         A.CHAR_ITM_ID AS CHAR_ITM_ID,														    \n");
		strBuff.append("                         (SELECT SCR_KOR FROM TN_ITM_LIST WHERE org_id = a.org_id and tbl_id = a.tbl_id and obj_var_id = '13999001' and ITM_ID = CHAR_ITM_ID) AS CHAR_ITM_NM,	    \n");
		strBuff.append("                         (SELECT CHAR_ITM_SN FROM TN_ITM_LIST WHERE org_id = a.org_id and tbl_id = a.tbl_id and obj_var_id = '13999001' and ITM_ID = CHAR_ITM_ID) AS CHAR_ITM_SN,   \n");
		strBuff.append("                         'test' as CHAR_UNIT_ID,														    \n");
		strBuff.append("                         B.DTVAL_CO AS DTVAL_CO,														    \n");
		strBuff.append("                         9 AS WGT_CO,														    \n");
		strBuff.append("                         A.UNIT_ID														    \n");
		strBuff.append("                 FROM    TN_DIM A, TN_DT B														    \n");
		strBuff.append("                 WHERE   A.ORG_ID = B.ORG_ID														    \n");
		strBuff.append("                         AND A.TBL_ID = B.TBL_ID														    \n");
		strBuff.append("                         AND A.ITM_RCGN_SN = B.ITM_RCGN_SN														    \n");
		strBuff.append("                         AND A.ORG_ID = '301'														    \n");
		strBuff.append("                         AND A.TBL_ID = 'DT_027Y118'														    \n");
		strBuff.append("                         AND B.PRD_SE = 'Y'														    \n");
		strBuff.append("                         and b.prd_de in ('1998', '1999')														    \n");
		strBuff.append("                         and a.ov_l1_id in ('13102160077UPJONG_CODE.D1541','13102160077UPJONG_CODE.D1545','13102160077UPJONG_CODE.D1553','13102160077UPJONG_CODE.D1554')														    \n");
		strBuff.append("                         and a.ov_l2_id in (SELECT itm_id FROM TN_ITM_LIST WHERE org_id = a.org_id and tbl_id = a.tbl_id AND OBJ_VAR_ID = (					    \n");
		strBuff.append("                                                                     SELECT  OBJ_VAR_ID												    \n");
		strBuff.append("                                                                     FROM    TN_OBJ_ITM_CLS											    \n");
		strBuff.append("                                                                     WHERE   ORG_ID = a.ORG_ID											    \n");
		strBuff.append("                                                                             AND TBL_ID = a.TBL_ID										    \n");
		strBuff.append("                                                                             AND VAR_ORD_SN = 2											    \n");
		strBuff.append("                                                                  ))														    \n");
		strBuff.append("                         and a.ov_l3_id in (SELECT itm_id FROM TN_ITM_LIST WHERE org_id = a.org_id and tbl_id = a.tbl_id AND OBJ_VAR_ID = (					    \n");
		strBuff.append("                                                                     SELECT  OBJ_VAR_ID												    \n");
		strBuff.append("                                                                     FROM    TN_OBJ_ITM_CLS											    \n");
		strBuff.append("                                                                     WHERE   ORG_ID = a.ORG_ID											    \n");
		strBuff.append("                                                                             AND TBL_ID = a.TBL_ID										    \n");
		strBuff.append("                                                                             AND VAR_ORD_SN = 3											    \n");
		strBuff.append("                                                                  ))														    \n");
		strBuff.append("                        order by ov_l1_sn, ov_l1_id, ov_l2_sn, ov_l2_id, ov_l3_sn, ov_l3_id, char_itm_sn, char_itm_id								    \n");
		//strBuff.append("                        ) where rownum <= 100								    \n");
		
		return strBuff.toString();
		
	}
	
}
