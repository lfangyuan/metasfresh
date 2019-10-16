package de.metas.esb.edi.model;


/** Generated Interface for EDI_cctop_901_991_v
 *  @author Adempiere (generated) 
 */
@SuppressWarnings("javadoc")
public interface I_EDI_cctop_901_991_v 
{

    /** TableName=EDI_cctop_901_991_v */
    public static final String Table_Name = "EDI_cctop_901_991_v";

    /** AD_Table_ID=540469 */
//    public static final int Table_ID = org.compiere.model.MTable.getTable_ID(Table_Name);

//    org.compiere.util.KeyNamePair Model = new org.compiere.util.KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 3 - Client - Org
     */
//    java.math.BigDecimal accessLevel = java.math.BigDecimal.valueOf(3);

    /** Load Meta Data */

	/**
	 * Get Mandant.
	 * Mandant für diese Installation.
	 *
	 * <br>Type: TableDir
	 * <br>Mandatory: true
	 * <br>Virtual Column: false
	 */
	public int getAD_Client_ID();

    /** Column definition for AD_Client_ID */
    public static final org.adempiere.model.ModelColumn<I_EDI_cctop_901_991_v, org.compiere.model.I_AD_Client> COLUMN_AD_Client_ID = new org.adempiere.model.ModelColumn<I_EDI_cctop_901_991_v, org.compiere.model.I_AD_Client>(I_EDI_cctop_901_991_v.class, "AD_Client_ID", org.compiere.model.I_AD_Client.class);
    /** Column name AD_Client_ID */
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";

	/**
	 * Set Organisation.
	 * Organisatorische Einheit des Mandanten
	 *
	 * <br>Type: Search
	 * <br>Mandatory: true
	 * <br>Virtual Column: false
	 */
	public void setAD_Org_ID (int AD_Org_ID);

	/**
	 * Get Organisation.
	 * Organisatorische Einheit des Mandanten
	 *
	 * <br>Type: Search
	 * <br>Mandatory: true
	 * <br>Virtual Column: false
	 */
	public int getAD_Org_ID();

    /** Column definition for AD_Org_ID */
    public static final org.adempiere.model.ModelColumn<I_EDI_cctop_901_991_v, org.compiere.model.I_AD_Org> COLUMN_AD_Org_ID = new org.adempiere.model.ModelColumn<I_EDI_cctop_901_991_v, org.compiere.model.I_AD_Org>(I_EDI_cctop_901_991_v.class, "AD_Org_ID", org.compiere.model.I_AD_Org.class);
    /** Column name AD_Org_ID */
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";

	/**
	 * Set Invoice.
	 * Invoice Identifier
	 *
	 * <br>Type: TableDir
	 * <br>Mandatory: false
	 * <br>Virtual Column: false
	 */
	public void setC_Invoice_ID (int C_Invoice_ID);

	/**
	 * Get Invoice.
	 * Invoice Identifier
	 *
	 * <br>Type: TableDir
	 * <br>Mandatory: false
	 * <br>Virtual Column: false
	 */
	public int getC_Invoice_ID();

	public org.compiere.model.I_C_Invoice getC_Invoice();

	public void setC_Invoice(org.compiere.model.I_C_Invoice C_Invoice);

    /** Column definition for C_Invoice_ID */
    public static final org.adempiere.model.ModelColumn<I_EDI_cctop_901_991_v, org.compiere.model.I_C_Invoice> COLUMN_C_Invoice_ID = new org.adempiere.model.ModelColumn<I_EDI_cctop_901_991_v, org.compiere.model.I_C_Invoice>(I_EDI_cctop_901_991_v.class, "C_Invoice_ID", org.compiere.model.I_C_Invoice.class);
    /** Column name C_Invoice_ID */
    public static final String COLUMNNAME_C_Invoice_ID = "C_Invoice_ID";

	/**
	 * Get Erstellt.
	 * Datum, an dem dieser Eintrag erstellt wurde
	 *
	 * <br>Type: DateTime
	 * <br>Mandatory: true
	 * <br>Virtual Column: false
	 */
	public java.sql.Timestamp getCreated();

    /** Column definition for Created */
    public static final org.adempiere.model.ModelColumn<I_EDI_cctop_901_991_v, Object> COLUMN_Created = new org.adempiere.model.ModelColumn<I_EDI_cctop_901_991_v, Object>(I_EDI_cctop_901_991_v.class, "Created", null);
    /** Column name Created */
    public static final String COLUMNNAME_Created = "Created";

	/**
	 * Get Erstellt durch.
	 * Nutzer, der diesen Eintrag erstellt hat
	 *
	 * <br>Type: Table
	 * <br>Mandatory: true
	 * <br>Virtual Column: false
	 */
	public int getCreatedBy();

    /** Column definition for CreatedBy */
    public static final org.adempiere.model.ModelColumn<I_EDI_cctop_901_991_v, org.compiere.model.I_AD_User> COLUMN_CreatedBy = new org.adempiere.model.ModelColumn<I_EDI_cctop_901_991_v, org.compiere.model.I_AD_User>(I_EDI_cctop_901_991_v.class, "CreatedBy", org.compiere.model.I_AD_User.class);
    /** Column name CreatedBy */
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";

	/**
	 * Set EDI_cctop_901_991_v.
	 *
	 * <br>Type: ID
	 * <br>Mandatory: false
	 * <br>Virtual Column: false
	 */
	public void setEDI_cctop_901_991_v_ID (int EDI_cctop_901_991_v_ID);

	/**
	 * Get EDI_cctop_901_991_v.
	 *
	 * <br>Type: ID
	 * <br>Mandatory: false
	 * <br>Virtual Column: false
	 */
	public int getEDI_cctop_901_991_v_ID();

    /** Column definition for EDI_cctop_901_991_v_ID */
    public static final org.adempiere.model.ModelColumn<I_EDI_cctop_901_991_v, Object> COLUMN_EDI_cctop_901_991_v_ID = new org.adempiere.model.ModelColumn<I_EDI_cctop_901_991_v, Object>(I_EDI_cctop_901_991_v.class, "EDI_cctop_901_991_v_ID", null);
    /** Column name EDI_cctop_901_991_v_ID */
    public static final String COLUMNNAME_EDI_cctop_901_991_v_ID = "EDI_cctop_901_991_v_ID";

	/**
	 * Set EDI_cctop_invoic_v.
	 *
	 * <br>Type: TableDir
	 * <br>Mandatory: false
	 * <br>Virtual Column: false
	 */
	public void setEDI_cctop_invoic_v_ID (int EDI_cctop_invoic_v_ID);

	/**
	 * Get EDI_cctop_invoic_v.
	 *
	 * <br>Type: TableDir
	 * <br>Mandatory: false
	 * <br>Virtual Column: false
	 */
	public int getEDI_cctop_invoic_v_ID();

	public de.metas.esb.edi.model.I_EDI_cctop_invoic_v getEDI_cctop_invoic_v();

	public void setEDI_cctop_invoic_v(de.metas.esb.edi.model.I_EDI_cctop_invoic_v EDI_cctop_invoic_v);

    /** Column definition for EDI_cctop_invoic_v_ID */
    public static final org.adempiere.model.ModelColumn<I_EDI_cctop_901_991_v, de.metas.esb.edi.model.I_EDI_cctop_invoic_v> COLUMN_EDI_cctop_invoic_v_ID = new org.adempiere.model.ModelColumn<I_EDI_cctop_901_991_v, de.metas.esb.edi.model.I_EDI_cctop_invoic_v>(I_EDI_cctop_901_991_v.class, "EDI_cctop_invoic_v_ID", de.metas.esb.edi.model.I_EDI_cctop_invoic_v.class);
    /** Column name EDI_cctop_invoic_v_ID */
    public static final String COLUMNNAME_EDI_cctop_invoic_v_ID = "EDI_cctop_invoic_v_ID";

	/**
	 * Set Referenznummer.
	 * Ihre Kunden- oder Lieferantennummer beim Geschäftspartner
	 *
	 * <br>Type: String
	 * <br>Mandatory: false
	 * <br>Virtual Column: false
	 */
	public void setESRReferenceNumber (java.lang.String ESRReferenceNumber);

	/**
	 * Get Referenznummer.
	 * Ihre Kunden- oder Lieferantennummer beim Geschäftspartner
	 *
	 * <br>Type: String
	 * <br>Mandatory: false
	 * <br>Virtual Column: false
	 */
	public java.lang.String getESRReferenceNumber();

    /** Column definition for ESRReferenceNumber */
    public static final org.adempiere.model.ModelColumn<I_EDI_cctop_901_991_v, Object> COLUMN_ESRReferenceNumber = new org.adempiere.model.ModelColumn<I_EDI_cctop_901_991_v, Object>(I_EDI_cctop_901_991_v.class, "ESRReferenceNumber", null);
    /** Column name ESRReferenceNumber */
    public static final String COLUMNNAME_ESRReferenceNumber = "ESRReferenceNumber";

	/**
	 * Set Aktiv.
	 * Der Eintrag ist im System aktiv
	 *
	 * <br>Type: YesNo
	 * <br>Mandatory: true
	 * <br>Virtual Column: false
	 */
	public void setIsActive (boolean IsActive);

	/**
	 * Get Aktiv.
	 * Der Eintrag ist im System aktiv
	 *
	 * <br>Type: YesNo
	 * <br>Mandatory: true
	 * <br>Virtual Column: false
	 */
	public boolean isActive();

    /** Column definition for IsActive */
    public static final org.adempiere.model.ModelColumn<I_EDI_cctop_901_991_v, Object> COLUMN_IsActive = new org.adempiere.model.ModelColumn<I_EDI_cctop_901_991_v, Object>(I_EDI_cctop_901_991_v.class, "IsActive", null);
    /** Column name IsActive */
    public static final String COLUMNNAME_IsActive = "IsActive";

	/**
	 * Set Satz.
	 * Rate or Tax or Exchange
	 *
	 * <br>Type: Amount
	 * <br>Mandatory: false
	 * <br>Virtual Column: false
	 */
	public void setRate (java.math.BigDecimal Rate);

	/**
	 * Get Satz.
	 * Rate or Tax or Exchange
	 *
	 * <br>Type: Amount
	 * <br>Mandatory: false
	 * <br>Virtual Column: false
	 */
	public java.math.BigDecimal getRate();

    /** Column definition for Rate */
    public static final org.adempiere.model.ModelColumn<I_EDI_cctop_901_991_v, Object> COLUMN_Rate = new org.adempiere.model.ModelColumn<I_EDI_cctop_901_991_v, Object>(I_EDI_cctop_901_991_v.class, "Rate", null);
    /** Column name Rate */
    public static final String COLUMNNAME_Rate = "Rate";

	/**
	 * Set Steuerbetrag.
	 * Steuerbetrag für diesen Beleg
	 *
	 * <br>Type: Amount
	 * <br>Mandatory: false
	 * <br>Virtual Column: false
	 */
	public void setTaxAmt (java.math.BigDecimal TaxAmt);

	/**
	 * Get Steuerbetrag.
	 * Steuerbetrag für diesen Beleg
	 *
	 * <br>Type: Amount
	 * <br>Mandatory: false
	 * <br>Virtual Column: false
	 */
	public java.math.BigDecimal getTaxAmt();

    /** Column definition for TaxAmt */
    public static final org.adempiere.model.ModelColumn<I_EDI_cctop_901_991_v, Object> COLUMN_TaxAmt = new org.adempiere.model.ModelColumn<I_EDI_cctop_901_991_v, Object>(I_EDI_cctop_901_991_v.class, "TaxAmt", null);
    /** Column name TaxAmt */
    public static final String COLUMNNAME_TaxAmt = "TaxAmt";

	/**
	 * Set Bezugswert.
	 * Bezugswert für die Berechnung der Steuer
	 *
	 * <br>Type: Amount
	 * <br>Mandatory: false
	 * <br>Virtual Column: false
	 */
	public void setTaxBaseAmt (java.math.BigDecimal TaxBaseAmt);

	/**
	 * Get Bezugswert.
	 * Bezugswert für die Berechnung der Steuer
	 *
	 * <br>Type: Amount
	 * <br>Mandatory: false
	 * <br>Virtual Column: false
	 */
	public java.math.BigDecimal getTaxBaseAmt();

    /** Column definition for TaxBaseAmt */
    public static final org.adempiere.model.ModelColumn<I_EDI_cctop_901_991_v, Object> COLUMN_TaxBaseAmt = new org.adempiere.model.ModelColumn<I_EDI_cctop_901_991_v, Object>(I_EDI_cctop_901_991_v.class, "TaxBaseAmt", null);
    /** Column name TaxBaseAmt */
    public static final String COLUMNNAME_TaxBaseAmt = "TaxBaseAmt";

	/**
	 * Set Total Amount.
	 * Total Amount
	 *
	 * <br>Type: Amount
	 * <br>Mandatory: false
	 * <br>Virtual Column: false
	 */
	public void setTotalAmt (java.math.BigDecimal TotalAmt);

	/**
	 * Get Total Amount.
	 * Total Amount
	 *
	 * <br>Type: Amount
	 * <br>Mandatory: false
	 * <br>Virtual Column: false
	 */
	public java.math.BigDecimal getTotalAmt();

    /** Column definition for TotalAmt */
    public static final org.adempiere.model.ModelColumn<I_EDI_cctop_901_991_v, Object> COLUMN_TotalAmt = new org.adempiere.model.ModelColumn<I_EDI_cctop_901_991_v, Object>(I_EDI_cctop_901_991_v.class, "TotalAmt", null);
    /** Column name TotalAmt */
    public static final String COLUMNNAME_TotalAmt = "TotalAmt";

	/**
	 * Get Aktualisiert.
	 * Datum, an dem dieser Eintrag aktualisiert wurde
	 *
	 * <br>Type: DateTime
	 * <br>Mandatory: true
	 * <br>Virtual Column: false
	 */
	public java.sql.Timestamp getUpdated();

    /** Column definition for Updated */
    public static final org.adempiere.model.ModelColumn<I_EDI_cctop_901_991_v, Object> COLUMN_Updated = new org.adempiere.model.ModelColumn<I_EDI_cctop_901_991_v, Object>(I_EDI_cctop_901_991_v.class, "Updated", null);
    /** Column name Updated */
    public static final String COLUMNNAME_Updated = "Updated";

	/**
	 * Get Aktualisiert durch.
	 * Nutzer, der diesen Eintrag aktualisiert hat
	 *
	 * <br>Type: Table
	 * <br>Mandatory: true
	 * <br>Virtual Column: false
	 */
	public int getUpdatedBy();

    /** Column definition for UpdatedBy */
    public static final org.adempiere.model.ModelColumn<I_EDI_cctop_901_991_v, org.compiere.model.I_AD_User> COLUMN_UpdatedBy = new org.adempiere.model.ModelColumn<I_EDI_cctop_901_991_v, org.compiere.model.I_AD_User>(I_EDI_cctop_901_991_v.class, "UpdatedBy", org.compiere.model.I_AD_User.class);
    /** Column name UpdatedBy */
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";
}
