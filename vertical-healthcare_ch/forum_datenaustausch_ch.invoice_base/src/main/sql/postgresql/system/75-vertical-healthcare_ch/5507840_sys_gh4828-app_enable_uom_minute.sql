-- 2018-12-10T16:32:34.597
-- I forgot to set the DICTIONARY_ID_COMMENTS System Configurator
UPDATE C_UOM SET IsActive='Y', StdPrecision=60, UOMType='TM',Updated=TO_TIMESTAMP('2018-12-10 16:32:34','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE C_UOM_ID=103
;

-- 2018-12-10T16:32:40.586
-- I forgot to set the DICTIONARY_ID_COMMENTS System Configurator
UPDATE C_UOM SET StdPrecision=0,Updated=TO_TIMESTAMP('2018-12-10 16:32:40','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE C_UOM_ID=103
;

-- 2018-12-10T16:32:58.427
-- I forgot to set the DICTIONARY_ID_COMMENTS System Configurator
INSERT INTO C_UOM_Conversion (AD_Client_ID,AD_Org_ID,Created,CreatedBy,C_UOM_Conversion_ID,C_UOM_ID,C_UOM_To_ID,DivideRate,IsActive,MultiplyRate,Updated,UpdatedBy) VALUES (0,0,TO_TIMESTAMP('2018-12-10 16:32:58','YYYY-MM-DD HH24:MI:SS'),100,540008,103,101,0.016666666667,'Y',60.000000000000,TO_TIMESTAMP('2018-12-10 16:32:58','YYYY-MM-DD HH24:MI:SS'),100)
;

-- 2018-12-10T16:33:21.970
-- I forgot to set the DICTIONARY_ID_COMMENTS System Configurator
UPDATE C_UOM SET UOMType='TM',Updated=TO_TIMESTAMP('2018-12-10 16:33:21','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE C_UOM_ID=101
;

