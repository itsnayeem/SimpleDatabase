# SimpleDatabase #

A simple in-memory database that supports transactions.

Usage:

    java -jar simpledatabase.jar

Input commands via stdin

Supported commands:

    SET [name] [value]
    GET [name]
    UNSET [name]
    EQUALTO [value]
    BEGIN
    ROLLBACK
    COMMIT
    END
