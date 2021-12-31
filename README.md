# co.codewizards.vaers

Collection of VAERS-related tools. Currently, there's only one tool: The import-tool `co.codewizards.vaers.imp`, but others may follow in the future.

## co.codewizards.vaers.imp

1. [Download the VAERS-zip-file](https://vaers.hhs.gov/data.html) -- it's large and the download takes a while. You can proceed with the next steps while the download is running.
2. Clone this repository and set up your IDE, i.e. import this Maven-project.
3. Run the main-class `VaersImport` without any parameters. It creates the configuration-file: `~/.vaers/vaers.properties`
4. Create an empty PostgreSQL-database for the import -- you may call it `vaers` ;-)
5. Configure `~/.vaers/vaers.properties` to match your new database.
6. Run `VaersImport` with the directory where you're currently downloading (and later unpacking) the VAERS-zip-file as first parameter. If the database is correctly configured in the `vaers.properties`, you should now have a few tables in it.
7. When the download completed, unpack the zip-file.
8. Run `VaersImport` again (i.e. repeat step 6) with the directory in which all the CSV-files are located. You can specify a parent-directory -- the program scans it recursively.
9. Start pgAdmin or another SQL-query-tool for your SELECTs.
10. Wait for a couple of hours. The import takes very long. You should see the progress in the text-output of the program and also you can already start querying the database. The INSERTs are done in individual transactions, thus you can already see the intermediate state while the import is still running.

**Important:** The import performs INSERTs only. It does no SELECTs and no UPDATEs for the sake of a faster performance. In order to avoid duplicate data, it performs a `TRUNCATE TABLE ... RESTART IDENTITY` for all VAERS-tables at the beginning of each run.

## Analysis of data

There are 3 different tables:
* `VAERSDATA`
* `VAERSSYMPTOMS`
* `VAERSVAX`

Each of them has a `VAERS_ID` which specifies one single vaccination-adverse-reaction-event. The `VAERS_ID` is the primary key of the table `VAERSDATA`. But each of these events can comprise multiple vaccines, hence there's a 1-n-relation from `VAERSDATA` to `VAERSVAX`. Also, there is a 1-n-relation from `VAERSDATA` to `VAERSSYMPTOMS` as a multitude of symptoms may exist.

### Example query: COVID19 vs. OTHER

The following query compares the quantity of unique `VAERS_ID`-values registered for a "COVID19"-vaccination with all other vaccinations:

```
select v.vax_type_group, v.died, count(*) as "count"
from (
	select distinct
	  case when vaersvax.vax_type = 'COVID19' then 'COVID19' else 'OTHER' end as vax_type_group,
	  vaersdata.died, vaersdata.vaers_id
	from vaersdata
	inner join vaersvax on vaersvax.vaers_id = vaersdata.vaers_id
) as v
group by v.vax_type_group, v.died
order by v.vax_type_group, v.died
;
```
Since a single VAERS-event may have multiple rows in table `vaersvax`, we use the `distinct` to make sure we do not count it twice for the same `vax_type_group`. Note: The above query may still count one event twice, if it has two different `vax_type_group`-values associated.

