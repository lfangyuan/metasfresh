import { Calendar, Year } from '../../support/utils/calendar';
import { ContractTransition } from '../../support/utils/contract_transition';
import { ConditionsType, ContractConditions, ProductAllocation } from '../../support/utils/contract_conditions';
import { LagerKonferenz, LagerKonferenzVersion } from '../../support/utils/contract_lagerKonferenz';

import { Product, ProductCategory, ProductPrice } from '../../support/utils/product';
import { Pricesystem } from '../../support/utils/pricesystem';
import { PriceList, PriceListVersion } from '../../support/utils/pricelist';
import { BPartner } from '../../support/utils/bpartner';
import { runProcessCreateContract } from '../../support/functions/contractFunctions';
import { appendHumanReadableNow, getLanguageSpecific } from '../../support/utils/utils';
import { StorageConferenceVersion } from '../../support/utils/storage_conferenceversion';

// pricing
let priceSystemName;
let priceListName;
let priceListVersionName;

// product
let productCategoryName;
let scrapProductName;
let processingFeeProductName;
let productionProductName;
let witholdingProductName;

let rawProductName;

let calendarName;
let contactTransitionName;
let serviceProductType;
let itemProductType;
let contractConditionsName;
let vendorName;
let lagerKonferenzName;
let validFrom;
let validTo;
let scrapFeeAmt;
let percentageScrapTreshhold;
let uomScrap;

// static
const currentYear = new Date().getFullYear();

// test
let countryName;
let priceListVersion;
let productPrice;

it('Read the fixture', function() {
  cy.fixture('contracts/flatrate_conditions_material_tracking_spec.json').then(f => {
    // pricing
    priceSystemName = appendHumanReadableNow(f['priceSystemName']);
    priceListName = appendHumanReadableNow(f['priceListName']);
    priceListVersionName = appendHumanReadableNow(f['priceListVersionName']);

    // product
    productCategoryName = appendHumanReadableNow(f['productCategoryName']);
    scrapProductName = appendHumanReadableNow(f['scrapProductName']);
    processingFeeProductName = appendHumanReadableNow(f['processingFeeProductName']);
    productionProductName = appendHumanReadableNow(f['productionProductName']);
    witholdingProductName = appendHumanReadableNow(f['witholdingProductName']);

    rawProductName = appendHumanReadableNow(f['rawProductName']);

    calendarName = appendHumanReadableNow(f['calendarName']);
    contactTransitionName = appendHumanReadableNow(f['contactTransitionName']);
    serviceProductType = f['serviceProductType'];
    itemProductType = f['itemProductType'];
    contractConditionsName = appendHumanReadableNow(f['contractConditionsName']);
    vendorName = appendHumanReadableNow(f['vendorName']);
    lagerKonferenzName = appendHumanReadableNow(f['lagerKonferenzName']);
    validFrom = f['validFrom'];
    validTo = f['validTo'];

    scrapFeeAmt = f['scrapFeeAmt'];
    percentageScrapTreshhold = f['percentageScrapTreshhold'];
    uomScrap = f['uomScrap'];
  });
});

it('Read country name', function() {
  cy.fixture('misc/misc_dictionary.json').then(dictionary => {
    countryName = getLanguageSpecific(dictionary, 'c_country_CH');
  });
});

it('Create calendar with years', function() {
  new Calendar()
    .setName(calendarName)
    .addYear(new Year(currentYear))
    .addYear(new Year(currentYear + 1))
    .apply();
});

it('Create transition', function() {
  new ContractTransition({ baseName: contactTransitionName }).setCalendar(calendarName).apply();
  cy.screenshot();
});

it('Create PriceList and Product Price', function() {
  // PriceList
  cy.fixture('price/pricesystem.json').then(priceSystemJson => {
    Object.assign(new Pricesystem(), priceSystemJson)
      .setName(priceSystemName)
      .apply();
  });

  // PriceListVersion
  cy.fixture('price/pricelistversion.json').then(priceListVersionJson => {
    priceListVersion = Object.assign(new PriceListVersion(), priceListVersionJson).setName(priceListVersionName);
  });

  // Product Price
  cy.fixture('product/product_price.json').then(productPriceJson => {
    productPrice = Object.assign(new ProductPrice(), productPriceJson).setPriceList(priceListName);
  });
});

it('Create LagerKonferenz PLV', function() {
  cy.fixture('price/pricelist.json').then(pricelistJson => {
    Object.assign(new PriceList(), pricelistJson)
      .setName(priceListName)
      .setCountry(countryName)
      .setCurrency('CHF')
      .setPriceSystem(priceSystemName)
      .addPriceListVersion(priceListVersion)
      .apply();
  });
});

it('Create LagerKonferenz products', function() {
  cy.fixture('product/simple_productCategory.json').then(productCategoryJson => {
    Object.assign(new ProductCategory(), productCategoryJson)
      .setName(productCategoryName)
      .apply();
  });

  createProduct(scrapProductName, serviceProductType, productCategoryName, productPrice);
  createProduct(processingFeeProductName, serviceProductType, productCategoryName, productPrice);
  createProduct(productionProductName, serviceProductType, productCategoryName, productPrice);
  createProduct(witholdingProductName, serviceProductType, productCategoryName, productPrice);
});

it('Create LagerKonferenz', function() {
  new LagerKonferenz({ baseName: lagerKonferenzName })
    .addSettingsLine(
      new LagerKonferenzVersion()
        .setValidFrom(validFrom)
        .setValidTo(validTo)
        .setScrapProduct(scrapProductName)
        .setScrapUOM('c_uom_KGM')
        .setScrapFeeAmt(scrapFeeAmt)
        .setPercentageScrapTreshhold(percentageScrapTreshhold)
        .setProcessingFeeProduct(processingFeeProductName)
        .setRegularProductionProduct(productionProductName)
        .setWitholdingProduct(witholdingProductName)
    )
    .apply();
});

it('Create conditions', function() {
  createProduct(rawProductName, itemProductType, productCategoryName, productPrice);
  new ContractConditions({ baseName: contractConditionsName })
    .setConditionsType(ConditionsType.QualityBased)
    .setTransition(contactTransitionName)
    .setLagerKonferenz(lagerKonferenzName)
    .addProductAllocation(new ProductAllocation().setProductCategory(productCategoryName).setProduct(rawProductName))
    .apply();
});

it('Create vendor', function() {
  cy.fixture('sales/simple_vendor.json').then(vendorJson => {
    new BPartner({ ...vendorJson, name: vendorName }).setVendorPricingSystem(priceSystemName).apply();
  });
  runProcessCreateContract(contractConditionsName);
});

it('Create new Storage conference version', function() {
  cy.fixture('contract/storage_conferenceversion.json').then(conferenceVersionJson => {
    Object.assign(new StorageConferenceVersion(), conferenceVersionJson)
      .setLagerKonferenz(lagerKonferenzName)
      .setProductProcessingFee(processingFeeProductName)
      .setProductWitholding(witholdingProductName)
      .setProductRegularPPOrder(productionProductName)
      .setProductScrap(scrapProductName)
      .setUOMScrap(uomScrap)
      .setValidFrom(validFrom)
      .setValidTo(validTo)
      .setPercentageScrapTreshhold(percentageScrapTreshhold)
      .setScrapFeeAmt(scrapFeeAmt)
      .apply();
  });
});

function createProduct(productName, productType, productCategoryName, productPrice) {
  cy.fixture('product/simple_product.json').then(productJson => {
    Object.assign(new Product(), productJson)
      .setName(productName)
      .setProductType(productType)
      .setProductCategory(productCategoryName)
      .addProductPrice(productPrice)
      .apply();
  });
}
