package gov.usgs.cida.pubs.domain;

import java.time.LocalDateTime;

public final class DeletedPublicationHelper {
	private DeletedPublicationHelper() {
	}

	public static final DeletedPublication SIX_SIX_ONE =
			new DeletedPublication(661,
					"sir20105033",
					"Deleted One",
					"10.3133/sir20105040",
					LocalDateTime.of(2018, 12, 31, 8, 10, 15),
					"pubgerone");

	public static final DeletedPublication SIX_SIX_TWO =
			new DeletedPublication(662,
					"ofr20111030",
					"Deleted Two",
					"10.1139/f75-258",
					LocalDateTime.of(2018, 12, 31, 8, 10, 15),
					"pubgerone");

	public static final DeletedPublication SIX_SIX_THREE =
			new DeletedPublication(663,
					"ds469",
					"Deleted Three",
					"10.1002/etc.5620091110",
					LocalDateTime.of(2017, 12, 31, 8, 10, 15),
					"pubgerone");

	public static final DeletedPublication SIX_SIX_FOUR =
			new DeletedPublication(664,
					"fs20103096",
					"Deleted Four",
					"10.1577/1548-8659(1961)90[404:ROTHLT]2.0.CO;2",
					LocalDateTime.of(2016, 12, 31, 8, 10, 15),
					"pubgertwo");

	public static final DeletedPublication SIX_SIX_FIVE =
			new DeletedPublication(665,
					"665",
					"Deleted Five",
					"10.3133/93891",
					LocalDateTime.of(2015, 12, 31, 8, 10, 15),
					"pubgerone");

	public static final DeletedPublication SIX_SIX_SEVEN = 
			new DeletedPublication(667,
					"6677",
					"Deleted Seven",
					"10.3133/667",
					null,
					"pubgerthree");

	public static final String TABLE_NAME = "deleted_publication";
	public static final String QUERY_TEXT = "select publication_id, index_id, title, doi_name,"
			+ " case when (now() - delete_date) > make_interval(mins => 1) then delete_date end delete_date, "
			+ " (now() - delete_date) < make_interval(mins => 1) delete_date_recent, delete_username from deleted_publication";
}
