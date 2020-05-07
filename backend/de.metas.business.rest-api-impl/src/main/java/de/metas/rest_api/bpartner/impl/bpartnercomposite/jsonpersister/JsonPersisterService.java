package de.metas.rest_api.bpartner.impl.bpartnercomposite.jsonpersister;

import static de.metas.util.Check.assumeNotEmpty;
import static de.metas.util.Check.isBlank;
import static de.metas.util.Check.isEmpty;
import static de.metas.util.lang.CoalesceUtil.coalesce;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.compiere.util.Env;
import org.slf4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import de.metas.bpartner.BPGroup;
import de.metas.bpartner.BPGroupId;
import de.metas.bpartner.BPGroupRepository;
import de.metas.bpartner.BPartnerId;
import de.metas.bpartner.GLN;
import de.metas.bpartner.composite.BPartner;
import de.metas.bpartner.composite.BPartnerBankAccount;
import de.metas.bpartner.composite.BPartnerComposite;
import de.metas.bpartner.composite.BPartnerCompositeAndContactId;
import de.metas.bpartner.composite.BPartnerContact;
import de.metas.bpartner.composite.BPartnerContactType;
import de.metas.bpartner.composite.BPartnerContactType.BPartnerContactTypeBuilder;
import de.metas.bpartner.composite.BPartnerLocation;
import de.metas.bpartner.composite.BPartnerLocationType;
import de.metas.bpartner.composite.BPartnerLocationType.BPartnerLocationTypeBuilder;
import de.metas.bpartner.composite.repository.BPartnerCompositeRepository;
import de.metas.bpartner.service.BPartnerContactQuery;
import de.metas.bpartner.service.BPartnerContactQuery.BPartnerContactQueryBuilder;
import de.metas.currency.CurrencyCode;
import de.metas.currency.CurrencyRepository;
import de.metas.i18n.BooleanWithReason;
import de.metas.i18n.Language;
import de.metas.i18n.TranslatableStrings;
import de.metas.logging.LogManager;
import de.metas.money.CurrencyId;
import de.metas.order.InvoiceRule;
import de.metas.organization.IOrgDAO;
import de.metas.organization.OrgId;
import de.metas.organization.OrgQuery;
import de.metas.rest_api.bpartner.impl.JsonRequestConsolidateService;
import de.metas.rest_api.bpartner.impl.bpartnercomposite.BPartnerCompositeRestUtils;
import de.metas.rest_api.bpartner.impl.bpartnercomposite.JsonRetrieverService;
import de.metas.rest_api.bpartner.request.JsonRequestBPartner;
import de.metas.rest_api.bpartner.request.JsonRequestBPartnerUpsertItem;
import de.metas.rest_api.bpartner.request.JsonRequestBankAccountUpsertItem;
import de.metas.rest_api.bpartner.request.JsonRequestBankAccountsUpsert;
import de.metas.rest_api.bpartner.request.JsonRequestComposite;
import de.metas.rest_api.bpartner.request.JsonRequestContact;
import de.metas.rest_api.bpartner.request.JsonRequestContactUpsert;
import de.metas.rest_api.bpartner.request.JsonRequestContactUpsertItem;
import de.metas.rest_api.bpartner.request.JsonRequestLocation;
import de.metas.rest_api.bpartner.request.JsonRequestLocationUpsert;
import de.metas.rest_api.bpartner.request.JsonRequestLocationUpsertItem;
import de.metas.rest_api.bpartner.response.JsonResponseBPartnerCompositeUpsertItem;
import de.metas.rest_api.bpartner.response.JsonResponseBPartnerCompositeUpsertItem.JsonResponseBPartnerCompositeUpsertItemBuilder;
import de.metas.rest_api.bpartner.response.JsonResponseUpsert;
import de.metas.rest_api.bpartner.response.JsonResponseUpsert.JsonResponseUpsertBuilder;
import de.metas.rest_api.bpartner.response.JsonResponseUpsertItem;
import de.metas.rest_api.bpartner.response.JsonResponseUpsertItem.JsonResponseUpsertItemBuilder;
import de.metas.rest_api.bpartner.response.JsonResponseUpsertItem.SyncOutcome;
import de.metas.rest_api.common.MetasfreshId;
import de.metas.rest_api.common.SyncAdvise;
import de.metas.rest_api.common.SyncAdvise.IfExists;
import de.metas.rest_api.exception.InvalidIdentifierException;
import de.metas.rest_api.exception.MissingPropertyException;
import de.metas.rest_api.exception.MissingResourceException;
import de.metas.rest_api.utils.IdentifierString;
import de.metas.rest_api.utils.IdentifierString.Type;
import de.metas.rest_api.utils.JsonConverters;
import de.metas.rest_api.utils.JsonExternalIds;
import de.metas.user.UserId;
import de.metas.util.Services;
import de.metas.util.StringUtils;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/*
 * #%L
 * de.metas.business.rest-api-impl
 * %%
 * Copyright (C) 2019 metas GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

@ToString
public class JsonPersisterService
{
	private static final Logger logger = LogManager.getLogger(JsonPersisterService.class);

	private final transient JsonRetrieverService jsonRetrieverService;
	private final transient JsonRequestConsolidateService jsonRequestConsolidateService;
	private final transient BPartnerCompositeRepository bpartnerCompositeRepository;
	private final transient BPGroupRepository bpGroupRepository;
	private final transient CurrencyRepository currencyRepository;

	@Getter
	private final String identifier;

	public JsonPersisterService(
			@NonNull final JsonRetrieverService jsonRetrieverService,
			@NonNull final JsonRequestConsolidateService jsonRequestConsolidateService,
			@NonNull final BPartnerCompositeRepository bpartnerCompositeRepository,
			@NonNull final BPGroupRepository bpGroupRepository,
			@NonNull final CurrencyRepository currencyRepository,
			@NonNull final String identifier)
	{
		this.jsonRetrieverService = jsonRetrieverService;
		this.jsonRequestConsolidateService = jsonRequestConsolidateService;
		this.bpartnerCompositeRepository = bpartnerCompositeRepository;
		this.bpGroupRepository = bpGroupRepository;
		this.currencyRepository = currencyRepository;

		this.identifier = assumeNotEmpty(identifier, "Param Identifier may not be empty");
	}

	public JsonResponseBPartnerCompositeUpsertItem persist(
			@NonNull final JsonRequestBPartnerUpsertItem requestItem,
			@NonNull final SyncAdvise parentSyncAdvise)
	{
		// TODO: add support to retrieve without changelog; we don't need changelog here;
		// but! make sure we don't screw up caching

		final String rawBpartnerIdentifier = requestItem.getBpartnerIdentifier();
		final IdentifierString bpartnerIdentifier = IdentifierString.of(rawBpartnerIdentifier);
		final Optional<BPartnerComposite> optionalBPartnerComposite = jsonRetrieverService.getBPartnerComposite(bpartnerIdentifier);

		final JsonResponseBPartnerCompositeUpsertItemUnderConstrunction resultBuilder = new JsonResponseBPartnerCompositeUpsertItemUnderConstrunction();
		resultBuilder.setJsonResponseBPartnerUpsertItemBuilder(JsonResponseUpsertItem.builder().identifier(rawBpartnerIdentifier));

		final JsonRequestComposite jsonRequestComposite = requestItem.getBpartnerComposite();
		final SyncAdvise effectiveSyncAdvise = coalesce(jsonRequestComposite.getSyncAdvise(), parentSyncAdvise);

		final BPartnerComposite bpartnerComposite;
		if (optionalBPartnerComposite.isPresent())
		{
			logger.debug("Found BPartner with id={} for identifier={} (orgCode={})", optionalBPartnerComposite.get().getBpartner().getId(), rawBpartnerIdentifier, jsonRequestComposite.getOrgCode());
			// load and mutate existing aggregation root
			bpartnerComposite = optionalBPartnerComposite.get();
			resultBuilder.setNewBPartner(false);
		}
		else
		{
			logger.debug("Found no BPartner for identifier={} (orgCode={})", rawBpartnerIdentifier, jsonRequestComposite.getOrgCode());
			if (effectiveSyncAdvise.isFailIfNotExists())
			{
				throw MissingResourceException.builder()
						.resourceName("bpartner")
						.resourceIdentifier(rawBpartnerIdentifier)
						.parentResource(jsonRequestComposite)
						.build()
						.setParameter("effectiveSyncAdvise", effectiveSyncAdvise);
			}
			// create new aggregation root
			logger.debug("Going to create a new bpartner-composite (orgCode={})", jsonRequestComposite.getOrgCode());
			bpartnerComposite = BPartnerComposite.builder().build();
			resultBuilder.setNewBPartner(true);
		}

		syncJsonToBPartnerComposite(
				resultBuilder,
				jsonRequestComposite,
				bpartnerComposite,
				effectiveSyncAdvise);

		return resultBuilder.build();
	}

	@Data
	private static final class JsonResponseBPartnerCompositeUpsertItemUnderConstrunction
	{
		private JsonResponseUpsertItemBuilder jsonResponseBPartnerUpsertItemBuilder;
		private boolean newBPartner;

		private ImmutableMap<String, JsonResponseUpsertItemBuilder> jsonResponseContactUpsertItems;

		private ImmutableMap<String, JsonResponseUpsertItemBuilder> jsonResponseLocationUpsertItems;

		private ImmutableMap<String, JsonResponseUpsertItemBuilder> jsonResponseBankAccountUpsertItems;

		public JsonResponseBPartnerCompositeUpsertItem build()
		{
			final JsonResponseBPartnerCompositeUpsertItemBuilder itemBuilder = JsonResponseBPartnerCompositeUpsertItem
					.builder()
					.responseBPartnerItem(jsonResponseBPartnerUpsertItemBuilder.build());

			final ImmutableList<JsonResponseUpsertItem> contactUpsertItems = jsonResponseContactUpsertItems
					.values()
					.stream()
					.map(JsonResponseUpsertItemBuilder::build)
					.collect(ImmutableList.toImmutableList());

			final ImmutableList<JsonResponseUpsertItem> locationUpsertItems = jsonResponseLocationUpsertItems
					.values()
					.stream()
					.map(JsonResponseUpsertItemBuilder::build)
					.collect(ImmutableList.toImmutableList());

			final ImmutableList<JsonResponseUpsertItem> bankAccountUpsertItems = jsonResponseBankAccountUpsertItems
					.values()
					.stream()
					.map(JsonResponseUpsertItemBuilder::build)
					.collect(ImmutableList.toImmutableList());

			return itemBuilder
					.responseContactItems(contactUpsertItems)
					.responseLocationItems(locationUpsertItems)
					.responseBankAccountItems(bankAccountUpsertItems)
					.build();
		}

	}

	public JsonResponseUpsertItem persist(
			@NonNull final IdentifierString contactIdentifier,
			@NonNull final JsonRequestContact jsonContact,
			@NonNull final SyncAdvise parentSyncAdvise)
	{
		final BPartnerContactQuery contactQuery = createContactQuery(contactIdentifier);
		final Optional<BPartnerCompositeAndContactId> optionalContactIdAndBPartner = bpartnerCompositeRepository.getByContact(contactQuery);

		final BPartnerContact contact;
		final BPartnerComposite bpartnerComposite;
		final SyncOutcome syncOutcome;
		if (optionalContactIdAndBPartner.isPresent())
		{
			final BPartnerCompositeAndContactId contactIdAndBPartner = optionalContactIdAndBPartner.get();

			bpartnerComposite = contactIdAndBPartner.getBpartnerComposite();

			contact = bpartnerComposite
					.extractContact(contactIdAndBPartner.getBpartnerContactId())
					.get(); // it's there, or we wouldn't be in this if-block
			syncOutcome = SyncOutcome.UPDATED;
		}
		else
		{
			if (parentSyncAdvise.isFailIfNotExists())
			{
				throw MissingResourceException.builder().resourceName("contact").resourceIdentifier(contactIdentifier.toJson()).parentResource(jsonContact).build()
						.setParameter("effectiveSyncAdvise", parentSyncAdvise);
			}
			if (jsonContact.getMetasfreshBPartnerId() == null)
			{
				throw new MissingPropertyException("JsonContact.metasfreshBPartnerId", jsonContact);
			}
			final BPartnerId bpartnerId = BPartnerId.ofRepoId(jsonContact.getMetasfreshBPartnerId().getValue());

			bpartnerComposite = bpartnerCompositeRepository.getById(bpartnerId);

			contact = BPartnerContact.builder().build();
			bpartnerComposite.getContacts().add(contact);
			syncOutcome = SyncOutcome.CREATED;
		}

		syncJsonToContact(contactIdentifier, jsonContact, contact, parentSyncAdvise);

		bpartnerCompositeRepository.save(bpartnerComposite);

		// might be different from contactIdentifier
		final IdentifierString identifierAfterUpdate = jsonRequestConsolidateService.extractIdentifier(contactIdentifier.getType(), contact);

		final Optional<BPartnerContact> persistedContact = bpartnerComposite.extractContact(BPartnerCompositeRestUtils.createContactFilterFor(identifierAfterUpdate));

		return JsonResponseUpsertItem.builder()
				.identifier(contactIdentifier.getRawIdentifierString())
				.metasfreshId(MetasfreshId.of(persistedContact.get().getId()))
				.syncOutcome(syncOutcome)
				.build();
	}

	private static BPartnerContactQuery createContactQuery(@NonNull final IdentifierString contactIdentifier)
	{
		final BPartnerContactQueryBuilder contactQuery = BPartnerContactQuery.builder();

		switch (contactIdentifier.getType())
		{
			case EXTERNAL_ID:
				contactQuery.externalId(JsonExternalIds.toExternalIdOrNull(contactIdentifier.asJsonExternalId()));
				break;
			case METASFRESH_ID:
				final UserId userId = contactIdentifier.asMetasfreshId(UserId::ofRepoId);
				contactQuery.userId(userId);
				break;
			case VALUE:
				contactQuery.value(contactIdentifier.asValue());
				break;
			default:
				throw new InvalidIdentifierException(contactIdentifier);
		}
		return contactQuery.build();
	}

	/**
	 * Adds or updates the given locations. Leaves all unrelated locations of the same bPartner untouched
	 */
	public Optional<JsonResponseUpsert> persistForBPartner(
			@NonNull final IdentifierString bpartnerIdentifier,
			@NonNull final JsonRequestLocationUpsert jsonRequestLocationUpsert,
			@NonNull final SyncAdvise parentSyncAdvise)
	{
		final Optional<BPartnerComposite> optBPartnerComposite = jsonRetrieverService.getBPartnerComposite(bpartnerIdentifier);
		if (!optBPartnerComposite.isPresent())
		{
			return Optional.empty(); // 404
		}

		final BPartnerComposite bpartnerComposite = optBPartnerComposite.get();
		final ShortTermLocationIndex shortTermIndex = new ShortTermLocationIndex(bpartnerComposite);

		final SyncAdvise effectiveSyncAdvise = coalesce(jsonRequestLocationUpsert.getSyncAdvise(), parentSyncAdvise);

		final List<JsonRequestLocationUpsertItem> requestItems = jsonRequestLocationUpsert.getRequestItems();

		final Map<String, JsonResponseUpsertItemBuilder> identifierToBuilder = new HashMap<>();
		for (final JsonRequestLocationUpsertItem requestItem : requestItems)
		{
			final JsonResponseUpsertItemBuilder responseItemBuilder = syncJsonLocation(requestItem, effectiveSyncAdvise, shortTermIndex);

			// we don't know the metasfreshId yet, so for now just store what we know into the map
			identifierToBuilder.put(requestItem.getLocationIdentifier(), responseItemBuilder);
		}

		bpartnerCompositeRepository.save(bpartnerComposite);

		// now collect the metasfreshIds that we got after having invoked save
		final JsonResponseUpsertBuilder response = JsonResponseUpsert.builder();
		for (final JsonRequestLocationUpsertItem requestItem : requestItems)
		{
			final IdentifierString locationIdentifier = IdentifierString.of(requestItem.getLocationIdentifier());
			final BPartnerLocation bpartnerLocation = shortTermIndex.extract(locationIdentifier);

			final JsonResponseUpsertItem responseItem = identifierToBuilder
					.get(requestItem.getLocationIdentifier())
					.metasfreshId(MetasfreshId.of(bpartnerLocation.getId()))
					.build();
			response.responseItem(responseItem);
		}

		return Optional.of(response.build());
	}

	public Optional<JsonResponseUpsert> persistForBPartner(
			@NonNull final IdentifierString bpartnerIdentifier,
			@NonNull final JsonRequestContactUpsert jsonContactUpsert,
			@NonNull final SyncAdvise parentSyncAdvise)
	{
		final Optional<BPartnerComposite> optBPartnerComposite = jsonRetrieverService.getBPartnerComposite(bpartnerIdentifier);
		if (!optBPartnerComposite.isPresent())
		{
			return Optional.empty(); // 404
		}

		final BPartnerComposite bpartnerComposite = optBPartnerComposite.get();
		final ShortTermContactIndex shortTermIndex = new ShortTermContactIndex(bpartnerComposite);

		final SyncAdvise effectiveSyncAdvise = coalesce(jsonContactUpsert.getSyncAdvise(), parentSyncAdvise);

		final Map<String, JsonResponseUpsertItemBuilder> identifierToBuilder = new HashMap<>();
		for (final JsonRequestContactUpsertItem requestItem : jsonContactUpsert.getRequestItems())
		{
			final JsonResponseUpsertItemBuilder responseItemBuilder = syncJsonContact(requestItem, effectiveSyncAdvise, shortTermIndex);

			// we don't know the metasfreshId yet, so for now just store what we know into the map
			identifierToBuilder.put(requestItem.getContactIdentifier(), responseItemBuilder);
		}

		bpartnerCompositeRepository.save(bpartnerComposite);

		// now collect what we got
		final JsonResponseUpsertBuilder response = JsonResponseUpsert.builder();
		for (final JsonRequestContactUpsertItem requestItem : jsonContactUpsert.getRequestItems())
		{
			final BPartnerContact bpartnerContact = bpartnerComposite.extractContactByHandle(requestItem.getContactIdentifier()).get();

			final JsonResponseUpsertItem responseItem = identifierToBuilder
					.get(requestItem.getContactIdentifier())
					.metasfreshId(MetasfreshId.of(bpartnerContact.getId()))
					.build();
			response.responseItem(responseItem);
		}

		return Optional.of(response.build());
	}

	public Optional<JsonResponseUpsert> persistForBPartner(
			@NonNull final IdentifierString bpartnerIdentifier,
			@NonNull final JsonRequestBankAccountsUpsert jsonBankAccountsUpsert,
			@NonNull final SyncAdvise parentSyncAdvise)
	{
		final BPartnerComposite bpartnerComposite = jsonRetrieverService.getBPartnerComposite(bpartnerIdentifier).orElse(null);
		if (bpartnerComposite == null)
		{
			return Optional.empty(); // 404
		}

		final ShortTermBankAccountIndex shortTermIndex = new ShortTermBankAccountIndex(bpartnerComposite);

		final SyncAdvise effectiveSyncAdvise = coalesce(jsonBankAccountsUpsert.getSyncAdvise(), parentSyncAdvise);

		final Map<String, JsonResponseUpsertItemBuilder> identifierToBuilder = new HashMap<>();
		for (final JsonRequestBankAccountUpsertItem requestItem : jsonBankAccountsUpsert.getRequestItems())
		{
			final JsonResponseUpsertItemBuilder responseItemBuilder = syncJsonBankAccount(requestItem, effectiveSyncAdvise, shortTermIndex);

			// we don't know the metasfreshId yet, so for now just store what we know into the map
			identifierToBuilder.put(requestItem.getIban(), responseItemBuilder);
		}

		bpartnerCompositeRepository.save(bpartnerComposite);

		// now collect what we got
		final JsonResponseUpsertBuilder response = JsonResponseUpsert.builder();
		for (final JsonRequestBankAccountUpsertItem requestItem : jsonBankAccountsUpsert.getRequestItems())
		{
			final String iban = requestItem.getIban();
			final BPartnerBankAccount bankAccount = shortTermIndex.extract(iban);

			final JsonResponseUpsertItem responseItem = identifierToBuilder
					.get(requestItem.getIban())
					.metasfreshId(MetasfreshId.of(bankAccount.getId()))
					.build();
			response.responseItem(responseItem);
		}

		return Optional.of(response.build());
	}

	private void syncJsonToBPartnerComposite(
			@NonNull final JsonResponseBPartnerCompositeUpsertItemUnderConstrunction resultBuilder,
			@NonNull final JsonRequestComposite jsonRequestComposite,
			@NonNull final BPartnerComposite bpartnerComposite,
			@NonNull final SyncAdvise parentSyncAdvise)
	{
		syncJsonToOrg(jsonRequestComposite, bpartnerComposite, parentSyncAdvise);

		final BooleanWithReason anythingWasSynced = syncJsonToBPartner(
				jsonRequestComposite,
				bpartnerComposite,
				resultBuilder.isNewBPartner(),
				parentSyncAdvise);
		if (anythingWasSynced.isTrue())
		{
			resultBuilder.getJsonResponseBPartnerUpsertItemBuilder().syncOutcome(resultBuilder.isNewBPartner() ? SyncOutcome.CREATED : SyncOutcome.UPDATED);
		}
		else
		{
			resultBuilder.getJsonResponseBPartnerUpsertItemBuilder().syncOutcome(SyncOutcome.NOTHING_DONE);
		}
		resultBuilder.setJsonResponseContactUpsertItems(syncJsonToContacts(jsonRequestComposite, bpartnerComposite, parentSyncAdvise));

		final ImmutableMap<String, JsonResponseUpsertItemBuilder> identifierToLocationResponse = syncJsonToLocations(jsonRequestComposite, bpartnerComposite, parentSyncAdvise);
		resultBuilder.setJsonResponseLocationUpsertItems(identifierToLocationResponse);

		resultBuilder.setJsonResponseBankAccountUpsertItems(syncJsonToBankAccounts(jsonRequestComposite, bpartnerComposite, parentSyncAdvise));

		bpartnerCompositeRepository.save(bpartnerComposite);

		//
		// supplement the metasfreshiId which we now have after the "save()"
		resultBuilder.getJsonResponseBPartnerUpsertItemBuilder().metasfreshId(MetasfreshId.of(bpartnerComposite.getBpartner().getId()));

		final ImmutableMap<String, JsonResponseUpsertItemBuilder> jsonResponseContactUpsertItemBuilders = resultBuilder.getJsonResponseContactUpsertItems();
		for (final JsonRequestContactUpsertItem requestItem : jsonRequestComposite.getContactsNotNull().getRequestItems())
		{
			final String originalIdentifier = requestItem.getContactIdentifier();

			final JsonResponseUpsertItemBuilder builder = jsonResponseContactUpsertItemBuilders.get(originalIdentifier);
			final Optional<BPartnerContact> contact = bpartnerComposite.extractContactByHandle(originalIdentifier);
			builder.metasfreshId(MetasfreshId.of(contact.get().getId()));
		}

		final ImmutableMap<String, JsonResponseUpsertItemBuilder> jsonResponseLocationUpsertItemBuilders = resultBuilder.getJsonResponseLocationUpsertItems();
		for (final JsonRequestLocationUpsertItem requestItem : jsonRequestComposite.getLocationsNotNull().getRequestItems())
		{
			final String originalIdentifier = requestItem.getLocationIdentifier();

			final JsonResponseUpsertItemBuilder builder = jsonResponseLocationUpsertItemBuilders.get(originalIdentifier);
			final Optional<BPartnerLocation> location = bpartnerComposite.extractLocationByHandle(originalIdentifier);
			builder.metasfreshId(MetasfreshId.of(location.get().getId()));
		}

		final ImmutableMap<String, JsonResponseUpsertItemBuilder> jsonResponseBankAccountUpsertItemBuilders = resultBuilder.getJsonResponseBankAccountUpsertItems();
		for (final JsonRequestBankAccountUpsertItem requestItem : jsonRequestComposite.getBankAccountsNotNull().getRequestItems())
		{
			final JsonResponseUpsertItemBuilder builder = jsonResponseBankAccountUpsertItemBuilders.get(requestItem.getIban());
			final Optional<BPartnerBankAccount> bankAccount = bpartnerComposite.getBankAccountByIBAN(requestItem.getIban());
			builder.metasfreshId(MetasfreshId.of(bankAccount.get().getId()));
		}
	}

	private void syncJsonToOrg(
			@NonNull final JsonRequestComposite jsonBPartnerComposite,
			@NonNull final BPartnerComposite bpartnerComposite,
			@NonNull final SyncAdvise parentSyncAdvise)
	{
		final SyncAdvise syncAdvise = coalesce(jsonBPartnerComposite.getSyncAdvise(), parentSyncAdvise);
		final boolean hasOrgId = bpartnerComposite.getOrgId() != null;

		if (hasOrgId && !syncAdvise.getIfExists().isUpdate())
		{
			return;
		}

		final OrgId orgId;
		final String orgCode = jsonBPartnerComposite.getOrgCode();
		if (!isEmpty(orgCode, true))
		{
			orgId = Services.get(IOrgDAO.class)
					.retrieveOrgIdBy(OrgQuery.ofValue(orgCode))
					.orElseThrow(() -> MissingResourceException.builder()
							.resourceName("organisation")
							.resourceIdentifier(orgCode).build());
		}
		else
		{
			orgId = Env.getOrgId();
		}
		bpartnerComposite.setOrgId(orgId);
	}

	private BooleanWithReason syncJsonToBPartner(
			@NonNull final JsonRequestComposite jsonBPartnerComposite,
			@NonNull final BPartnerComposite bpartnerComposite,
			final boolean bpartnerCompositeIsNew,
			@NonNull final SyncAdvise parentSyncAdvise)
	{
		final JsonRequestBPartner jsonBPartner = jsonBPartnerComposite.getBpartner();
		if (jsonBPartner == null)
		{
			return BooleanWithReason.falseBecause("JsonRequestComposite does not include a JsonRequestBPartner");
		}

		// note that if the BPartner wouldn't exists, we weren't here
		final SyncAdvise effCompositeSyncAdvise = coalesce(jsonBPartnerComposite.getSyncAdvise(), parentSyncAdvise);

		if (bpartnerCompositeIsNew && effCompositeSyncAdvise.isFailIfNotExists())
		{
			throw MissingResourceException.builder().resourceName("bpartner").parentResource(jsonBPartnerComposite).build();
		}

		final BPartner bpartner = bpartnerComposite.getBpartner();
		final SyncAdvise effectiveSyncAdvise = coalesce(jsonBPartner.getSyncAdvise(), effCompositeSyncAdvise);
		final IfExists ifExistsAdvise = effectiveSyncAdvise.getIfExists();

		if (!bpartnerCompositeIsNew && !ifExistsAdvise.isUpdate())
		{
			return BooleanWithReason.falseBecause("JsonRequestBPartner exists and effectiveSyncAdvise.ifExists=" + ifExistsAdvise);
		}

		final boolean isUpdateRemove = ifExistsAdvise.isUpdateRemove();

		// active
		if (jsonBPartner.getActive() != null)
		{
			bpartner.setActive(jsonBPartner.getActive());
		}
		if (jsonBPartner.isActiveSet())
		{
			if (jsonBPartner.getActive() == null)
			{
				logger.debug("Ignoring boolean property \"active\" : null ");
			}
			else
			{
				bpartner.setActive(jsonBPartner.getActive());
			}
		}

		// code / value
		if (jsonBPartner.isCodeSet())
		{
			bpartner.setValue(StringUtils.trim(jsonBPartner.getCode()));
		}
		else if (isUpdateRemove)
		{
			bpartner.setValue(null);
		}

		// globalId
		if (jsonBPartner.isGlobalIdset())
		{
			bpartner.setGlobalId(StringUtils.trim(jsonBPartner.getGlobalId()));
		}
		else if (isUpdateRemove)
		{
			bpartner.setGlobalId(null);
		}

		// companyName
		if (jsonBPartner.isCompanyNameSet())
		{
			bpartner.setCompanyName(StringUtils.trim(jsonBPartner.getCompanyName()));
		}
		else if (isUpdateRemove)
		{
			bpartner.setCompanyName(null);
		}

		// name
		if (jsonBPartner.isNameSet())
		{
			bpartner.setName(StringUtils.trim(jsonBPartner.getName()));
		}
		else if (isUpdateRemove)
		{
			bpartner.setName(null);
		}

		// name2
		if (jsonBPartner.isName2Set())
		{
			bpartner.setName2(StringUtils.trim(jsonBPartner.getName2()));
		}
		else if (isUpdateRemove)
		{
			bpartner.setName2(null);
		}

		// name3
		if (jsonBPartner.isName3Set())
		{
			bpartner.setName3(StringUtils.trim(jsonBPartner.getName3()));
		}
		else if (isUpdateRemove)
		{
			bpartner.setName3(null);
		}

		// externalId
		if (jsonBPartner.isExternalIdSet())
		{
			bpartner.setExternalId(JsonConverters.fromJsonOrNull(jsonBPartner.getExternalId()));
		}
		else if (isUpdateRemove)
		{
			bpartner.setExternalId(null);
		}

		if (jsonBPartner.isCustomerSet())
		{
			if (jsonBPartner.getCustomer() == null)
			{
				logger.debug("Ignoring boolean property \"customer\" : null ");
			}
			else
			{
			bpartner.setCustomer(jsonBPartner.getCustomer());
		}
		}
		if (jsonBPartner.isVendorSet())
		{
			if (jsonBPartner.getVendor() == null)
			{
				logger.debug("Ignoring boolean property \"vendor\" : null ");
			}
			else
			{
			bpartner.setVendor(jsonBPartner.getVendor());
		}
		}

		// group - attempt to fall back to default group
		if (jsonBPartner.isGroupSet())
		{
			if (jsonBPartner.getGroup() == null)
			{
				logger.debug("Setting \"groupId\" to null; -> will attempt to insert default groupId");
				bpartner.setGroupId(null);
			}
			else
			{
				final Optional<BPGroup> optionalBPGroup = bpGroupRepository
						.getByNameAndOrgId(jsonBPartner.getGroup(), bpartnerComposite.getOrgId());

				final BPGroup bpGroup;
				if (optionalBPGroup.isPresent())
				{
					bpGroup = optionalBPGroup.get();
					bpGroup.setName(jsonBPartner.getGroup().trim());
				}
				else
				{
					bpGroup = BPGroup.of(bpartnerComposite.getOrgId(), null, jsonBPartner.getGroup().trim());
				}

				final BPGroupId bpGroupId = bpGroupRepository.save(bpGroup);
				bpartner.setGroupId(bpGroupId);
			}
		}
		else if(isUpdateRemove)
		{
			logger.debug("Setting \"groupId\" to null; -> will attempt to insert default groupId");
			bpartner.setGroupId(null);
		}
		if (bpartner.getGroupId() == null)
		{
			final Optional<BPGroup> optionalBPGroup = bpGroupRepository.getDefaultGroup();
			if (!optionalBPGroup.isPresent())
			{
				throw MissingResourceException.builder()
						.resourceName("group")
						.detail(TranslatableStrings.constant("JsonRequestBPartner specifies no bpartner-group and there is no default group"))
						.build();
			}
			bpartner.setGroupId(optionalBPGroup.get().getId());
			logger.debug("\"groupId\" = null; -> using default groupId={}", bpartner.getGroupId().getRepoId());
		}

		// language
		if (jsonBPartner.isLanguageSet())
		{
			bpartner.setLanguage(Language.asLanguage(StringUtils.trim(jsonBPartner.getLanguage())));
		}
		else if (isUpdateRemove)
		{
			bpartner.setLanguage(null);
		}

		// invoiceRule
		if (jsonBPartner.isInvoiceRuleSet())
		{
			if (jsonBPartner.getInvoiceRule() == null)
			{
				bpartner.setInvoiceRule(null);
			}
			else
			{
				bpartner.setInvoiceRule(InvoiceRule.ofCode(jsonBPartner.getInvoiceRule().toString()));
			}
		}
		else if (isUpdateRemove)
		{
			bpartner.setInvoiceRule(null);
		}

		// metasfreshId - we will never update it

		// parentId
		if (jsonBPartner.isParentIdSet())
		{
			// TODO make sure in the repo that the parent-bpartner is reachable
			bpartner.setParentId(BPartnerId.ofRepoIdOrNull(MetasfreshId.toValue(jsonBPartner.getParentId())));
		}
		else if (isUpdateRemove)
		{
			bpartner.setParentId(null);
		}

		// phone
		if (jsonBPartner.isPhoneSet())
		{
			bpartner.setPhone(StringUtils.trim(jsonBPartner.getPhone()));
		}
		else if (isUpdateRemove)
		{
			bpartner.setPhone(null);
		}

		// url
		if (jsonBPartner.isUrlSet())
		{
			bpartner.setUrl(StringUtils.trim(jsonBPartner.getUrl()));
		}
		else if (isUpdateRemove)
		{
			bpartner.setUrl(null);
		}

		// url2
		if (jsonBPartner.isUrl2Set())
		{
			bpartner.setUrl2(StringUtils.trim(jsonBPartner.getUrl2()));
		}
		else if (isUpdateRemove)
		{
			bpartner.setUrl2(null);
		}

		// url3
		if (jsonBPartner.isUrl3Set())
		{
			bpartner.setUrl3(StringUtils.trim(jsonBPartner.getUrl3()));
		}
		else if (isUpdateRemove)
		{
			bpartner.setUrl3(null);
		}

		return BooleanWithReason.TRUE;
	}

	private ImmutableMap<String, JsonResponseUpsertItemBuilder> syncJsonToContacts(
			@NonNull final JsonRequestComposite jsonBPartnerComposite,
			@NonNull final BPartnerComposite bpartnerComposite,
			@NonNull final SyncAdvise parentSyncAdvise)
	{
		final ShortTermContactIndex shortTermIndex = new ShortTermContactIndex(bpartnerComposite);

		resetDefaultFlagsIfNeeded(jsonBPartnerComposite, shortTermIndex);

		final JsonRequestContactUpsert contacts = jsonBPartnerComposite.getContactsNotNull();

		final SyncAdvise contactsSyncAdvise = coalesce(contacts.getSyncAdvise(), jsonBPartnerComposite.getSyncAdvise(), parentSyncAdvise);

		final ImmutableMap.Builder<String, JsonResponseUpsertItemBuilder> result = ImmutableMap.builder();
		for (final JsonRequestContactUpsertItem contactRequestItem : contacts.getRequestItems())
		{
			result.put(
					contactRequestItem.getContactIdentifier(),
					syncJsonContact(contactRequestItem, contactsSyncAdvise, shortTermIndex));
		}

		if (contactsSyncAdvise.getIfExists().isUpdateRemove())
		{
			// deactivate the remaining bpartner locations that we did not see
			bpartnerComposite.getContacts().removeAll(shortTermIndex.getUnusedContacts());
		}

		return result.build();
	}

	/**
	 * If the json contacts have default flags set, then this method unsets all corresponding default flags of the shortTermIndex's {@link BPartnerContact}s.
	 */
	private void resetDefaultFlagsIfNeeded(
			@NonNull final JsonRequestComposite jsonBPartnerComposite,
			@NonNull final ShortTermContactIndex shortTermIndex)
	{
		final List<JsonRequestContactUpsertItem> contactRequestItems = jsonBPartnerComposite.getContactsNotNull().getRequestItems();

		boolean hasDefaultContact = false;
		boolean hasBillToDefault = false;
		boolean hasShipToDefault = false;
		boolean hasPurchaseDefault = false;
		boolean hasSalesDefault = false;
		for (final JsonRequestContactUpsertItem contactRequestItem : contactRequestItems)
		{
			final JsonRequestContact contact = contactRequestItem.getContact();
			final Boolean defaultContact = contact.getDefaultContact();
			if (!hasDefaultContact && defaultContact != null && defaultContact)
			{
				hasDefaultContact = true;
			}
			final Boolean billToDefault = contact.getBillToDefault();
			if (!hasBillToDefault && billToDefault != null && billToDefault)
			{
				hasBillToDefault = true;
			}
			final Boolean shipToDefault = contact.getShipToDefault();
			if (!hasShipToDefault && shipToDefault != null && shipToDefault)
			{
				hasShipToDefault = true;
			}
			final Boolean purchaseDefault = contact.getPurchaseDefault();
			if (!hasPurchaseDefault && purchaseDefault != null && purchaseDefault)
			{
				hasPurchaseDefault = true;
			}
			final Boolean salesDefault = contact.getSalesDefault();
			if (!hasSalesDefault && salesDefault != null && salesDefault)
			{
				hasSalesDefault = true;
			}
		}
		if (hasDefaultContact)
		{
			shortTermIndex.resetDefaultContactFlags();
		}
		if (hasBillToDefault)
		{
			shortTermIndex.resetBillToDefaultFlags();
		}
		if (hasShipToDefault)
		{
			shortTermIndex.resetShipToDefaultFlags();
		}
		if (hasPurchaseDefault)
		{
			shortTermIndex.resetPurchaseDefaultFlags();
		}
		if (hasSalesDefault)
		{
			shortTermIndex.resetSalesDefaultFlags();
		}
	}

	private JsonResponseUpsertItemBuilder syncJsonContact(
			@NonNull final JsonRequestContactUpsertItem jsonContact,
			@NonNull final SyncAdvise parentSyncAdvise,
			@NonNull final ShortTermContactIndex shortTermIndex)
	{
		final IdentifierString contactIdentifier = IdentifierString.of(jsonContact.getContactIdentifier());
		final BPartnerContact existingContact = shortTermIndex.extract(contactIdentifier);

		final JsonResponseUpsertItemBuilder result = JsonResponseUpsertItem.builder()
				.identifier(jsonContact.getContactIdentifier());

		final BPartnerContact contact;
		if (existingContact != null)
		{
			contact = existingContact;
			result.syncOutcome(SyncOutcome.UPDATED);
		}
		else
		{
			if (parentSyncAdvise.isFailIfNotExists())
			{
				throw MissingResourceException.builder().resourceName("contact").resourceIdentifier(jsonContact.getContactIdentifier()).parentResource(jsonContact).build()
						.setParameter("parentSyncAdvise", parentSyncAdvise);
			}
			else if (Type.METASFRESH_ID.equals(contactIdentifier.getType()))
			{
				throw MissingResourceException.builder().resourceName("contact").resourceIdentifier(jsonContact.getContactIdentifier()).parentResource(jsonContact)
						.detail(TranslatableStrings.constant("With this type, only updates are allowed."))
						.build()
						.setParameter("parentSyncAdvise", parentSyncAdvise);
			}
			contact = shortTermIndex.newContact(contactIdentifier);
			result.syncOutcome(SyncOutcome.CREATED);
		}

		syncJsonToContact(contactIdentifier, jsonContact.getContact(), contact, parentSyncAdvise);
		return result;
	}

	private void syncJsonToContact(
			@NonNull final IdentifierString contactIdentifier,
			@NonNull final JsonRequestContact jsonBPartnerContact,
			@NonNull final BPartnerContact contact,
			@NonNull final SyncAdvise parentSyncAdvise)
	{
		final SyncAdvise syncAdvise = coalesce(jsonBPartnerContact.getSyncAdvise(), parentSyncAdvise);
		final boolean isUpdateRemove = syncAdvise.getIfExists().isUpdateRemove();

		contact.addHandle(contactIdentifier.getRawIdentifierString());

		// active
		if (jsonBPartnerContact.isActiveSet())
		{
			if (jsonBPartnerContact.getActive() == null)
			{
				logger.debug("Ignoring boolean property \"active\" : null ");
			}
			else
			{
			contact.setActive(jsonBPartnerContact.getActive());
		}
		}

		// email
		if (jsonBPartnerContact.isEmailSet())
		{
			contact.setEmail(StringUtils.trim(jsonBPartnerContact.getEmail()));
		}
		else if (isUpdateRemove)
		{
			contact.setEmail(null);
		}

		// externalId
		if (jsonBPartnerContact.isExternalIdSet())
		{
			contact.setExternalId(JsonConverters.fromJsonOrNull(jsonBPartnerContact.getExternalId()));
		}
		else if (isUpdateRemove)
		{
			contact.setExternalId(null);
		}

		// firstName
		if (jsonBPartnerContact.isFirstNameSet())
		{
			contact.setFirstName(StringUtils.trim(jsonBPartnerContact.getFirstName()));
		}
		else if (isUpdateRemove)
		{
			contact.setFirstName(null);
		}

		// lastName
		if (jsonBPartnerContact.isLastNameSet())
		{
			contact.setLastName(StringUtils.trim(jsonBPartnerContact.getLastName()));
		}
		else if (isUpdateRemove)
		{
			contact.setLastName(null);
		}

		// metasfreshBPartnerId - never updated;

		// metasfreshId - never updated;

		// name
		if (jsonBPartnerContact.isNameSet())
		{
			contact.setName(StringUtils.trim(jsonBPartnerContact.getName()));
		}
		else if (isUpdateRemove)
		{
			contact.setName(null);
		}

		// value
		if (jsonBPartnerContact.isCodeSet())
		{
			contact.setValue(StringUtils.trim(jsonBPartnerContact.getCode()));
		}
		else if (isUpdateRemove)
		{
			contact.setValue(null);
		}

		// description
		if (jsonBPartnerContact.isDescriptionSet())
		{
			contact.setDescription(StringUtils.trim(jsonBPartnerContact.getDescription()));
		}
		else if (isUpdateRemove)
		{
			contact.setDescription(null);
		}

		// phone
		if (jsonBPartnerContact.isPhoneSet())
		{
			contact.setPhone(StringUtils.trim(jsonBPartnerContact.getPhone()));
		}
		else if (isUpdateRemove)
		{
			contact.setPhone(null);
		}

		// fax
		if (jsonBPartnerContact.isFaxSet())
		{
			contact.setFax(StringUtils.trim(jsonBPartnerContact.getFax()));
		}
		else if (isUpdateRemove)
		{
			contact.setFax(null);
		}

		// mobilePhone
		if (jsonBPartnerContact.isMobilePhoneSet())
		{
			contact.setMobilePhone(StringUtils.trim(jsonBPartnerContact.getMobilePhone()));
		}
		else if (isUpdateRemove)
		{
			contact.setMobilePhone(null);
		}

		// newsletter
		if (jsonBPartnerContact.isNewsletterSet())
		{
			if (jsonBPartnerContact.getNewsletter() == null)
			{
				logger.debug("Ignoring boolean property \"newsLetter\" : null ");
			}
			else
			{
			contact.setNewsletter(jsonBPartnerContact.getNewsletter());
		}
		}

		final BPartnerContactType bpartnerContactType = syncJsonToContactType(jsonBPartnerContact);
		contact.setContactType(bpartnerContactType);
	}

	private BPartnerContactType syncJsonToContactType(@NonNull final JsonRequestContact jsonBPartnerContact)
	{
		final BPartnerContactTypeBuilder contactType = BPartnerContactType.builder();

		if (jsonBPartnerContact.isDefaultContactSet())
		{
			if (jsonBPartnerContact.getDefaultContact() == null)
			{
				logger.debug("Ignoring boolean property \"defaultContact\" : null ");
			}
			else
			{
				contactType.defaultContact(jsonBPartnerContact.getDefaultContact());
			}
		}
		if (jsonBPartnerContact.isShipToDefaultSet())
		{
			if (jsonBPartnerContact.getShipToDefault() == null)
			{
				logger.debug("Ignoring boolean property \"shipToDefault\" : null ");
			}
			else
			{
				contactType.shipToDefault(jsonBPartnerContact.getShipToDefault());
			}
		}
		if (jsonBPartnerContact.isBillToDefaultSet())
		{
			if (jsonBPartnerContact.getBillToDefault() == null)
			{
				logger.debug("Ignoring boolean property \"billToDefault\" : null ");
			}
			else
			{
				contactType.billToDefault(jsonBPartnerContact.getBillToDefault());
			}
		}
		if (jsonBPartnerContact.isPurchaseSet())
		{
			if (jsonBPartnerContact.getPurchase() == null)
			{
				logger.debug("Ignoring boolean property \"purchase\" : null ");
			}
			else
			{
				contactType.purchase(jsonBPartnerContact.getPurchase());
			}
		}
		if (jsonBPartnerContact.isPurchaseDefaultSet())
		{
			if (jsonBPartnerContact.getPurchaseDefault() == null)
			{
				logger.debug("Ignoring boolean property \"purchaseDefault\" : null ");
			}
			else
			{
				contactType.purchaseDefault(jsonBPartnerContact.getPurchaseDefault());
			}
		}
		if (jsonBPartnerContact.isSalesSet())
		{
			if (jsonBPartnerContact.getSales() == null)
			{
				logger.debug("Ignoring boolean property \"sales\" : null ");
			}
			else
			{
				contactType.sales(jsonBPartnerContact.getSales());
			}
		}
		if (jsonBPartnerContact.isSalesDefaultSet())
		{
			if (jsonBPartnerContact.getSalesDefault() == null)
			{
				logger.debug("Ignoring boolean property \"salesDefault\" : null ");
			}
			else
			{
				contactType.salesDefault(jsonBPartnerContact.getSalesDefault());
			}
		}
		if (jsonBPartnerContact.isSubjectMatterSet())
		{
			if (jsonBPartnerContact.getSubjectMatter() == null)
			{
				logger.debug("Ignoring boolean property \"subjectMatter\" : null ");
			}
			else
			{
				contactType.subjectMatter(jsonBPartnerContact.getSubjectMatter());
			}
		}

		BPartnerContactType ct = contactType.build();
		return ct;
	}

	private ImmutableMap<String, JsonResponseUpsertItemBuilder> syncJsonToLocations(
			@NonNull final JsonRequestComposite jsonBPartnerComposite,
			@NonNull final BPartnerComposite bpartnerComposite,
			@NonNull final SyncAdvise parentSyncAdvise)
	{
		final ShortTermLocationIndex shortTermIndex = new ShortTermLocationIndex(bpartnerComposite);

		resetDefaultFlagsIfNeeded(jsonBPartnerComposite, shortTermIndex);

		final JsonRequestLocationUpsert locations = jsonBPartnerComposite.getLocationsNotNull();

		final SyncAdvise locationsSyncAdvise = coalesce(locations.getSyncAdvise(), jsonBPartnerComposite.getSyncAdvise(), parentSyncAdvise);

		final ImmutableMap.Builder<String, JsonResponseUpsertItemBuilder> result = ImmutableMap.builder();
		for (final JsonRequestLocationUpsertItem locationRequestItem : locations.getRequestItems())
		{
			result.put(
					locationRequestItem.getLocationIdentifier(),
					syncJsonLocation(locationRequestItem, locationsSyncAdvise, shortTermIndex));
		}

		if (locationsSyncAdvise.getIfExists().isUpdateRemove())
		{
			// deactivate the remaining bpartner locations that we did not see
			bpartnerComposite.getLocations().removeAll(shortTermIndex.getUnusedLocations());
		}
		return result.build();
	}

	private ImmutableMap<String, JsonResponseUpsertItemBuilder> syncJsonToBankAccounts(
			@NonNull final JsonRequestComposite jsonBPartnerComposite,
			@NonNull final BPartnerComposite bpartnerComposite,
			@NonNull final SyncAdvise parentSyncAdvise)
	{
		final ShortTermBankAccountIndex shortTermIndex = new ShortTermBankAccountIndex(bpartnerComposite);

		final JsonRequestBankAccountsUpsert bankAccounts = jsonBPartnerComposite.getBankAccountsNotNull();
		final SyncAdvise bankAccountsSyncAdvise = coalesce(bankAccounts.getSyncAdvise(), jsonBPartnerComposite.getSyncAdvise(), parentSyncAdvise);

		final ImmutableMap.Builder<String, JsonResponseUpsertItemBuilder> result = ImmutableMap.builder();
		for (final JsonRequestBankAccountUpsertItem bankAccountRequestItem : bankAccounts.getRequestItems())
		{
			result.put(
					bankAccountRequestItem.getIban(),
					syncJsonBankAccount(bankAccountRequestItem, bankAccountsSyncAdvise, shortTermIndex));
		}

		if (bankAccountsSyncAdvise.getIfExists().isUpdateRemove())
		{
			// deactivate the remaining bpartner locations that we did not see
			bpartnerComposite.getBankAccounts().removeAll(shortTermIndex.getRemainingBankAccounts());
		}

		return result.build();
	}

	private JsonResponseUpsertItemBuilder syncJsonBankAccount(
			@NonNull final JsonRequestBankAccountUpsertItem jsonBankAccount,
			@NonNull final SyncAdvise parentSyncAdvise,
			@NonNull final ShortTermBankAccountIndex shortTermIndex)
	{
		final String iban = jsonBankAccount.getIban();
		final BPartnerBankAccount existingBankAccount = shortTermIndex.extract(iban);

		final JsonResponseUpsertItemBuilder resultBuilder = JsonResponseUpsertItem.builder()
				.identifier(iban);

		final BPartnerBankAccount bankAccount;
		if (existingBankAccount != null)
		{
			bankAccount = existingBankAccount;
			shortTermIndex.remove(existingBankAccount.getId());
			resultBuilder.syncOutcome(SyncOutcome.UPDATED);
		}
		else
		{
			if (parentSyncAdvise.isFailIfNotExists())
			{
				throw MissingResourceException.builder().resourceName("bankAccount").resourceIdentifier(iban).parentResource(jsonBankAccount).build()
						.setParameter("parentSyncAdvise", parentSyncAdvise);
			}

			final CurrencyId currencyId = extractCurrencyIdOrNull(jsonBankAccount);
			if (currencyId == null)
			{
				throw MissingResourceException.builder().resourceName("bankAccount.currencyId").resourceIdentifier(iban).parentResource(jsonBankAccount).build()
						.setParameter("parentSyncAdvise", parentSyncAdvise);
			}

			bankAccount = shortTermIndex.newBankAccount(iban, currencyId);
			resultBuilder.syncOutcome(SyncOutcome.CREATED);
		}

		syncJsonToBankAccount(jsonBankAccount, bankAccount, parentSyncAdvise);

		return resultBuilder;
	}

	private void syncJsonToBankAccount(
			@NonNull final JsonRequestBankAccountUpsertItem jsonBankAccount,
			@NonNull final BPartnerBankAccount bankAccount,
			@NonNull final SyncAdvise parentSyncAdvise)
	{
		final SyncAdvise syncAdvise = coalesce(jsonBankAccount.getSyncAdvise(), parentSyncAdvise);
		final boolean isUpdateRemove = syncAdvise.getIfExists().isUpdateRemove();

		// active
		if (jsonBankAccount.getActive() != null)
		{
			bankAccount.setActive(jsonBankAccount.getActive());
		}

		// currency
		final CurrencyId currencyId = extractCurrencyIdOrNull(jsonBankAccount);
		if (currencyId != null)
		{
			bankAccount.setCurrencyId(currencyId);
		}
		else if (isUpdateRemove)
		{
			bankAccount.setCurrencyId(null);
		}

	}

	private CurrencyId extractCurrencyIdOrNull(final JsonRequestBankAccountUpsertItem jsonBankAccount)
	{
		if (isBlank(jsonBankAccount.getCurrencyCode()))
		{
			return null;
		}

		final CurrencyCode currencyCode = CurrencyCode.ofThreeLetterCode(jsonBankAccount.getCurrencyCode().trim());
		return currencyRepository.getCurrencyIdByCurrencyCode(currencyCode);
	}

	/**
	 * If the json locations have default flags set, then this method unsets all corresponding default flags of the shortTermIndex's {@link BPartnerLocation}s.
	 */
	private void resetDefaultFlagsIfNeeded(
			@NonNull final JsonRequestComposite jsonBPartnerComposite,
			@NonNull final ShortTermLocationIndex shortTermIndex)
	{
		boolean hasBillToDefault = false;
		boolean hasShipToDefault = false;

		final List<JsonRequestLocationUpsertItem> locationRequestItems = jsonBPartnerComposite
				.getLocationsNotNull()
				.getRequestItems();
		for (final JsonRequestLocationUpsertItem locationRequestItem : locationRequestItems)
		{
			final JsonRequestLocation location = locationRequestItem.getLocation();
			final Boolean billToDefault = location.getBillToDefault();
			if (!hasBillToDefault && billToDefault != null && billToDefault)
			{
				hasBillToDefault = true;
			}
			final Boolean shipToDefault = location.getShipToDefault();
			if (!hasShipToDefault && shipToDefault != null && shipToDefault)
			{
				hasShipToDefault = true;
			}
		}
		if (hasBillToDefault)
		{
			shortTermIndex.resetBillToDefaultFlags();
		}
		if (hasShipToDefault)
		{
			shortTermIndex.resetShipToDefaultFlags();
		}
	}

	private JsonResponseUpsertItemBuilder syncJsonLocation(
			@NonNull final JsonRequestLocationUpsertItem locationUpsertItem,
			@NonNull final SyncAdvise parentSyncAdvise,
			@NonNull final ShortTermLocationIndex shortTermIndex)
	{
		final IdentifierString locationIdentifier = IdentifierString.of(locationUpsertItem.getLocationIdentifier());
		final BPartnerLocation existingLocation = shortTermIndex.extract(locationIdentifier);

		final JsonResponseUpsertItemBuilder resultBuilder = JsonResponseUpsertItem.builder()
				.identifier(locationUpsertItem.getLocationIdentifier());

		final BPartnerLocation location;
		if (existingLocation != null)
		{
			location = existingLocation;
			resultBuilder.syncOutcome(SyncOutcome.UPDATED);
		}
		else
		{
			if (parentSyncAdvise.isFailIfNotExists())
			{
				throw MissingResourceException.builder()
						.resourceName("location")
						.resourceIdentifier(locationUpsertItem.getLocationIdentifier())
						.parentResource(locationUpsertItem)
						.detail(TranslatableStrings.constant("Type of locationlocationIdentifier=" + locationIdentifier.getType()))
						.build()
						.setParameter("effectiveSyncAdvise", parentSyncAdvise);
			}
			else if (Type.METASFRESH_ID.equals(locationIdentifier.getType()))
			{
				throw MissingResourceException.builder()
						.resourceName("location")
						.resourceIdentifier(locationUpsertItem.getLocationIdentifier())
						.parentResource(locationUpsertItem)
						.detail(TranslatableStrings.constant("Type of locationlocationIdentifier=" + locationIdentifier.getType() + "; with this identifier-type, only updates are allowed."))
						.build()
						.setParameter("effectiveSyncAdvise", parentSyncAdvise);
			}
			location = shortTermIndex.newLocation(locationIdentifier);
			resultBuilder.syncOutcome(SyncOutcome.CREATED);
		}
		location.addHandle(locationUpsertItem.getLocationIdentifier());
		syncJsonToLocation(locationUpsertItem.getLocation(), location, parentSyncAdvise);

		return resultBuilder;
	}

	private void syncJsonToLocation(
			@NonNull final JsonRequestLocation jsonBPartnerLocation,
			@NonNull final BPartnerLocation location,
			@NonNull final SyncAdvise parentSyncAdvise)
	{
		final SyncAdvise syncAdvise = coalesce(jsonBPartnerLocation.getSyncAdvise(), parentSyncAdvise);

		// active
		if (jsonBPartnerLocation.isActiveSet())
		{
			if (jsonBPartnerLocation.getActive() == null)
			{
				logger.debug("Ignoring boolean property \"active\" : null ");
			}
			else
			{
			location.setActive(jsonBPartnerLocation.getActive());
		}
		}

		final boolean isUpdateRemove = syncAdvise.getIfExists().isUpdateRemove();

		// name
		if (jsonBPartnerLocation.isNameSet())
		{
			location.setName(StringUtils.trim(jsonBPartnerLocation.getName()));
		}
		else if (isUpdateRemove)
		{
			location.setName(null);
		}

		// bpartnerName
		if (jsonBPartnerLocation.isNameSet())
		{
			location.setBpartnerName(StringUtils.trim(jsonBPartnerLocation.getBpartnerName()));
		}
		else if (isUpdateRemove)
		{
			location.setBpartnerName(null);
		}

		// address1
		if (jsonBPartnerLocation.isAddress1Set())
		{
			location.setAddress1(StringUtils.trim(jsonBPartnerLocation.getAddress1()));
		}
		else if (isUpdateRemove)
		{
			location.setAddress1(null);
		}

		// address2
		if (jsonBPartnerLocation.isAddress2Set())
		{
			location.setAddress2(StringUtils.trim(jsonBPartnerLocation.getAddress2()));
		}
		else if (isUpdateRemove)
		{
			location.setAddress2(null);
		}
		// address3
		if (jsonBPartnerLocation.isAddress3Set())
		{
			location.setAddress3(StringUtils.trim(jsonBPartnerLocation.getAddress3()));
		}
		else if (isUpdateRemove)
		{
			location.setAddress3(null);
		}

		// address4
		if (jsonBPartnerLocation.isAddress4Set())
		{
			location.setAddress4(StringUtils.trim(jsonBPartnerLocation.getAddress4()));
		}
		else if (isUpdateRemove)
		{
			location.setAddress4(null);
		}

		// city
		if (jsonBPartnerLocation.isCitySet())
		{
			location.setCity(StringUtils.trim(jsonBPartnerLocation.getCity()));
		}
		else if (isUpdateRemove)
		{
			location.setCity(null);
		}

		// countryCode
		if (jsonBPartnerLocation.isCountryCodeSet())
		{
			location.setCountryCode(StringUtils.trim(jsonBPartnerLocation.getCountryCode()));
		}
		else if (isUpdateRemove)
		{
			location.setCountryCode(null);
		}

		// district
		if (jsonBPartnerLocation.isDistrictSet())
		{
			location.setDistrict(StringUtils.trim(jsonBPartnerLocation.getDistrict()));
		}
		else if (isUpdateRemove)
		{
			location.setDistrict(null);
		}

		// externalId
		if (jsonBPartnerLocation.isExternalIdSet())
		{
			location.setExternalId(JsonConverters.fromJsonOrNull(jsonBPartnerLocation.getExternalId()));
		}
		else if (isUpdateRemove)
		{
			location.setExternalId(null);
		}

		// gln
		if (jsonBPartnerLocation.isGlnSet())
		{
		final GLN gln = GLN.ofNullableString(jsonBPartnerLocation.getGln());
			location.setGln(gln);
		}
		else if (isUpdateRemove)
		{
			location.setGln(null);
		}

		// poBox
		if (jsonBPartnerLocation.isPoBoxSet())
		{
			location.setPoBox(StringUtils.trim(jsonBPartnerLocation.getPoBox()));
		}
		else if (isUpdateRemove)
		{
			location.setPoBox(null);
		}

		// postal
		if (jsonBPartnerLocation.isPostalSet())
		{
			location.setPostal(StringUtils.trim(jsonBPartnerLocation.getPostal()));
		}
		else if (isUpdateRemove)
		{
			location.setPostal(null);
		}

		// region
		if (jsonBPartnerLocation.isRegionSet())
		{
			location.setRegion(StringUtils.trim(jsonBPartnerLocation.getRegion()));
		}
		else if (isUpdateRemove)
		{
			location.setRegion(null);
		}

		final BPartnerLocationType locationType = syncJsonToLocationType(jsonBPartnerLocation);
		location.setLocationType(locationType);
	}

	private BPartnerLocationType syncJsonToLocationType(@NonNull final JsonRequestLocation jsonBPartnerLocation)
	{
		final BPartnerLocationTypeBuilder locationType = BPartnerLocationType.builder();

		if (jsonBPartnerLocation.isBillToSet())
		{
			if (jsonBPartnerLocation.getBillTo() == null)
			{
				logger.debug("Ignoring boolean property \"billTo\" : null ");
}
			else
			{
				locationType.billTo(jsonBPartnerLocation.getBillTo());
			}
		}
		if (jsonBPartnerLocation.isBillToDefaultSet())
		{
			if (jsonBPartnerLocation.getBillToDefault() == null)
			{
				logger.debug("Ignoring boolean property \"billToDefault\" : null ");
			}
			else
			{
				locationType.billToDefault(jsonBPartnerLocation.getBillToDefault());
			}
		}
		if (jsonBPartnerLocation.isShipToSet())
		{
			if (jsonBPartnerLocation.getShipTo() == null)
			{
				logger.debug("Ignoring boolean property \"shipTo\" : null ");
			}
			else
			{
				locationType.shipTo(jsonBPartnerLocation.getShipTo());
			}
		}
		if (jsonBPartnerLocation.isShipToDefaultSet())
		{
			if (jsonBPartnerLocation.getShipToDefault() == null)
			{
				logger.debug("Ignoring boolean property \"shipToDefault\" : null ");
			}
			else
			{
				locationType.shipToDefault(jsonBPartnerLocation.getShipToDefault());
			}
		}

		return locationType.build();
	}
}