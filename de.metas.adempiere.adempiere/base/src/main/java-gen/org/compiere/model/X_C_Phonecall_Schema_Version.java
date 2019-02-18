/** Generated Model - DO NOT CHANGE */
package org.compiere.model;

import java.sql.ResultSet;
import java.util.Properties;

/** Generated Model for C_Phonecall_Schema_Version
 *  @author Adempiere (generated) 
 */
@SuppressWarnings("javadoc")
public class X_C_Phonecall_Schema_Version extends org.compiere.model.PO implements I_C_Phonecall_Schema_Version, org.compiere.model.I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 1231626038L;

    /** Standard Constructor */
    public X_C_Phonecall_Schema_Version (Properties ctx, int C_Phonecall_Schema_Version_ID, String trxName)
    {
      super (ctx, C_Phonecall_Schema_Version_ID, trxName);
      /** if (C_Phonecall_Schema_Version_ID == 0)
        {
			setC_Phonecall_Schema_ID (0);
			setC_Phonecall_Schema_Version_ID (0);
			setIsMonthly (false); // N
			setIsWeekly (false); // N
			setName (null);
			setValidFrom (new Timestamp( System.currentTimeMillis() ));
        } */
    }

    /** Load Constructor */
    public X_C_Phonecall_Schema_Version (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }


    /** Load Meta Data */
    @Override
    protected org.compiere.model.POInfo initPO (Properties ctx)
    {
      org.compiere.model.POInfo poi = org.compiere.model.POInfo.getPOInfo (ctx, Table_Name, get_TrxName());
      return poi;
    }

	@Override
	public org.compiere.model.I_C_Phonecall_Schema getC_Phonecall_Schema() throws RuntimeException
	{
		return get_ValueAsPO(COLUMNNAME_C_Phonecall_Schema_ID, org.compiere.model.I_C_Phonecall_Schema.class);
	}

	@Override
	public void setC_Phonecall_Schema(org.compiere.model.I_C_Phonecall_Schema C_Phonecall_Schema)
	{
		set_ValueFromPO(COLUMNNAME_C_Phonecall_Schema_ID, org.compiere.model.I_C_Phonecall_Schema.class, C_Phonecall_Schema);
	}

	/** Set Anrufliste.
		@param C_Phonecall_Schema_ID Anrufliste	  */
	@Override
	public void setC_Phonecall_Schema_ID (int C_Phonecall_Schema_ID)
	{
		if (C_Phonecall_Schema_ID < 1) 
			set_Value (COLUMNNAME_C_Phonecall_Schema_ID, null);
		else 
			set_Value (COLUMNNAME_C_Phonecall_Schema_ID, Integer.valueOf(C_Phonecall_Schema_ID));
	}

	/** Get Anrufliste.
		@return Anrufliste	  */
	@Override
	public int getC_Phonecall_Schema_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Phonecall_Schema_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Anruflistenversion.
		@param C_Phonecall_Schema_Version_ID Anruflistenversion	  */
	@Override
	public void setC_Phonecall_Schema_Version_ID (int C_Phonecall_Schema_Version_ID)
	{
		if (C_Phonecall_Schema_Version_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_Phonecall_Schema_Version_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_Phonecall_Schema_Version_ID, Integer.valueOf(C_Phonecall_Schema_Version_ID));
	}

	/** Get Anruflistenversion.
		@return Anruflistenversion	  */
	@Override
	public int getC_Phonecall_Schema_Version_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Phonecall_Schema_Version_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Every Month.
		@param EveryMonth 
		Every x Months
	  */
	@Override
	public void setEveryMonth (int EveryMonth)
	{
		set_Value (COLUMNNAME_EveryMonth, Integer.valueOf(EveryMonth));
	}

	/** Get Every Month.
		@return Every x Months
	  */
	@Override
	public int getEveryMonth () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_EveryMonth);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Every Week.
		@param EveryWeek 
		Every x weeks
	  */
	@Override
	public void setEveryWeek (int EveryWeek)
	{
		set_Value (COLUMNNAME_EveryWeek, Integer.valueOf(EveryWeek));
	}

	/** Get Every Week.
		@return Every x weeks
	  */
	@Override
	public int getEveryWeek () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_EveryWeek);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Monthly.
		@param IsMonthly Monthly	  */
	@Override
	public void setIsMonthly (boolean IsMonthly)
	{
		set_Value (COLUMNNAME_IsMonthly, Boolean.valueOf(IsMonthly));
	}

	/** Get Monthly.
		@return Monthly	  */
	@Override
	public boolean isMonthly () 
	{
		Object oo = get_Value(COLUMNNAME_IsMonthly);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Weekly.
		@param IsWeekly Weekly	  */
	@Override
	public void setIsWeekly (boolean IsWeekly)
	{
		set_Value (COLUMNNAME_IsWeekly, Boolean.valueOf(IsWeekly));
	}

	/** Get Weekly.
		@return Weekly	  */
	@Override
	public boolean isWeekly () 
	{
		Object oo = get_Value(COLUMNNAME_IsWeekly);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Day of the Month.
		@param MonthDay 
		Day of the month 1 to 28/29/30/31
	  */
	@Override
	public void setMonthDay (int MonthDay)
	{
		set_Value (COLUMNNAME_MonthDay, Integer.valueOf(MonthDay));
	}

	/** Get Day of the Month.
		@return Day of the month 1 to 28/29/30/31
	  */
	@Override
	public int getMonthDay () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MonthDay);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Name.
		@param Name 
		Alphanumeric identifier of the entity
	  */
	@Override
	public void setName (java.lang.String Name)
	{
		set_Value (COLUMNNAME_Name, Name);
	}

	/** Get Name.
		@return Alphanumeric identifier of the entity
	  */
	@Override
	public java.lang.String getName () 
	{
		return (java.lang.String)get_Value(COLUMNNAME_Name);
	}

	/** Set Freitag.
		@param OnFriday 
		Freitags verfügbar
	  */
	@Override
	public void setOnFriday (boolean OnFriday)
	{
		set_Value (COLUMNNAME_OnFriday, Boolean.valueOf(OnFriday));
	}

	/** Get Freitag.
		@return Freitags verfügbar
	  */
	@Override
	public boolean isOnFriday () 
	{
		Object oo = get_Value(COLUMNNAME_OnFriday);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Montag.
		@param OnMonday 
		Montags verfügbar
	  */
	@Override
	public void setOnMonday (boolean OnMonday)
	{
		set_Value (COLUMNNAME_OnMonday, Boolean.valueOf(OnMonday));
	}

	/** Get Montag.
		@return Montags verfügbar
	  */
	@Override
	public boolean isOnMonday () 
	{
		Object oo = get_Value(COLUMNNAME_OnMonday);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Samstag.
		@param OnSaturday 
		Samstags verfügbar
	  */
	@Override
	public void setOnSaturday (boolean OnSaturday)
	{
		set_Value (COLUMNNAME_OnSaturday, Boolean.valueOf(OnSaturday));
	}

	/** Get Samstag.
		@return Samstags verfügbar
	  */
	@Override
	public boolean isOnSaturday () 
	{
		Object oo = get_Value(COLUMNNAME_OnSaturday);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Sonntag.
		@param OnSunday 
		Sonntags verfügbar
	  */
	@Override
	public void setOnSunday (boolean OnSunday)
	{
		set_Value (COLUMNNAME_OnSunday, Boolean.valueOf(OnSunday));
	}

	/** Get Sonntag.
		@return Sonntags verfügbar
	  */
	@Override
	public boolean isOnSunday () 
	{
		Object oo = get_Value(COLUMNNAME_OnSunday);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Donnerstag.
		@param OnThursday 
		Donnerstags verfügbar
	  */
	@Override
	public void setOnThursday (boolean OnThursday)
	{
		set_Value (COLUMNNAME_OnThursday, Boolean.valueOf(OnThursday));
	}

	/** Get Donnerstag.
		@return Donnerstags verfügbar
	  */
	@Override
	public boolean isOnThursday () 
	{
		Object oo = get_Value(COLUMNNAME_OnThursday);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Dienstag.
		@param OnTuesday 
		Dienstags verfügbar
	  */
	@Override
	public void setOnTuesday (boolean OnTuesday)
	{
		set_Value (COLUMNNAME_OnTuesday, Boolean.valueOf(OnTuesday));
	}

	/** Get Dienstag.
		@return Dienstags verfügbar
	  */
	@Override
	public boolean isOnTuesday () 
	{
		Object oo = get_Value(COLUMNNAME_OnTuesday);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Mittwoch.
		@param OnWednesday 
		Mittwochs verfügbar
	  */
	@Override
	public void setOnWednesday (boolean OnWednesday)
	{
		set_Value (COLUMNNAME_OnWednesday, Boolean.valueOf(OnWednesday));
	}

	/** Get Mittwoch.
		@return Mittwochs verfügbar
	  */
	@Override
	public boolean isOnWednesday () 
	{
		Object oo = get_Value(COLUMNNAME_OnWednesday);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Bereitstellungszeit Mo.
		@param PreparationTime_1 
		Preparation time for monday
	  */
	@Override
	public void setPreparationTime_1 (java.sql.Timestamp PreparationTime_1)
	{
		set_Value (COLUMNNAME_PreparationTime_1, PreparationTime_1);
	}

	/** Get Bereitstellungszeit Mo.
		@return Preparation time for monday
	  */
	@Override
	public java.sql.Timestamp getPreparationTime_1 () 
	{
		return (java.sql.Timestamp)get_Value(COLUMNNAME_PreparationTime_1);
	}

	/** Set Bereitstellungszeit Di.
		@param PreparationTime_2 
		Preparation time for tuesday
	  */
	@Override
	public void setPreparationTime_2 (java.sql.Timestamp PreparationTime_2)
	{
		set_Value (COLUMNNAME_PreparationTime_2, PreparationTime_2);
	}

	/** Get Bereitstellungszeit Di.
		@return Preparation time for tuesday
	  */
	@Override
	public java.sql.Timestamp getPreparationTime_2 () 
	{
		return (java.sql.Timestamp)get_Value(COLUMNNAME_PreparationTime_2);
	}

	/** Set Bereitstellungszeit Mi.
		@param PreparationTime_3 
		Preparation time for wednesday
	  */
	@Override
	public void setPreparationTime_3 (java.sql.Timestamp PreparationTime_3)
	{
		set_Value (COLUMNNAME_PreparationTime_3, PreparationTime_3);
	}

	/** Get Bereitstellungszeit Mi.
		@return Preparation time for wednesday
	  */
	@Override
	public java.sql.Timestamp getPreparationTime_3 () 
	{
		return (java.sql.Timestamp)get_Value(COLUMNNAME_PreparationTime_3);
	}

	/** Set Bereitstellungszeit Do.
		@param PreparationTime_4 
		Preparation time for thursday
	  */
	@Override
	public void setPreparationTime_4 (java.sql.Timestamp PreparationTime_4)
	{
		set_Value (COLUMNNAME_PreparationTime_4, PreparationTime_4);
	}

	/** Get Bereitstellungszeit Do.
		@return Preparation time for thursday
	  */
	@Override
	public java.sql.Timestamp getPreparationTime_4 () 
	{
		return (java.sql.Timestamp)get_Value(COLUMNNAME_PreparationTime_4);
	}

	/** Set Bereitstellungszeit Fr.
		@param PreparationTime_5 
		Preparation time for Friday
	  */
	@Override
	public void setPreparationTime_5 (java.sql.Timestamp PreparationTime_5)
	{
		set_Value (COLUMNNAME_PreparationTime_5, PreparationTime_5);
	}

	/** Get Bereitstellungszeit Fr.
		@return Preparation time for Friday
	  */
	@Override
	public java.sql.Timestamp getPreparationTime_5 () 
	{
		return (java.sql.Timestamp)get_Value(COLUMNNAME_PreparationTime_5);
	}

	/** Set Bereitstellungszeit Sa.
		@param PreparationTime_6 
		Preparation time for Saturday
	  */
	@Override
	public void setPreparationTime_6 (java.sql.Timestamp PreparationTime_6)
	{
		set_Value (COLUMNNAME_PreparationTime_6, PreparationTime_6);
	}

	/** Get Bereitstellungszeit Sa.
		@return Preparation time for Saturday
	  */
	@Override
	public java.sql.Timestamp getPreparationTime_6 () 
	{
		return (java.sql.Timestamp)get_Value(COLUMNNAME_PreparationTime_6);
	}

	/** Set Bereitstellungszeit So.
		@param PreparationTime_7 
		Preparation time for Sunday
	  */
	@Override
	public void setPreparationTime_7 (java.sql.Timestamp PreparationTime_7)
	{
		set_Value (COLUMNNAME_PreparationTime_7, PreparationTime_7);
	}

	/** Get Bereitstellungszeit So.
		@return Preparation time for Sunday
	  */
	@Override
	public java.sql.Timestamp getPreparationTime_7 () 
	{
		return (java.sql.Timestamp)get_Value(COLUMNNAME_PreparationTime_7);
	}

	/** Set Gültig ab.
		@param ValidFrom 
		Gültig ab inklusiv (erster Tag)
	  */
	@Override
	public void setValidFrom (java.sql.Timestamp ValidFrom)
	{
		set_Value (COLUMNNAME_ValidFrom, ValidFrom);
	}

	/** Get Gültig ab.
		@return Gültig ab inklusiv (erster Tag)
	  */
	@Override
	public java.sql.Timestamp getValidFrom () 
	{
		return (java.sql.Timestamp)get_Value(COLUMNNAME_ValidFrom);
	}
}