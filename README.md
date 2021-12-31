# co.codewizards.vaers

Collection of VAERS-related tools. Currently, there's only one tool: The import-tool `co.codewizards.vaers.imp`, but others may follow in the future.

## co.codewizards.vaers.imp

1. Download the VAERS-zip-file from https://vaers.hhs.gov/data.html -- it's large and the download takes a while. You can proceed with the next steps while the download is running.
2. Clone this repository and set up your IDE, i.e. import this Maven-project (should be supported and easy with all major IDEs).
3. Run the main-class `VaersImport` without any parameters. It creates the configuration-file: `~/.vaers/vaers.properties`
4. Create an empty PostgreSQL-database for the import -- you may call it `vaers` ;-)
5. Configure `~/.vaers/vaers.properties` to match your new database.
6. Run `VaersImport` with the directory where you're currently downloading (and later unpacking) the VAERS-zip-file as first parameter. If the database is correctly configured in the `vaers.properties`, you should now have a few tables in it.
7. When the download completed, unpack the zip-file.
8. Run `VaersImport` again (i.e. repeat step 6) with the directory in which all the CSV-files are located. You can specify a parent-directory -- the program scans it recursively.
9. Start pgAdmin or another SQL-query-tool for your SELECTs.
10. Wait for a couple of hours. The import takes very long. You should see the progress in the text-output of the program and also you can already start querying the database. The INSERTs are done in individual transactions, thus you can already see the intermediate state while the import is still running.
