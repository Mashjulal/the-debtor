package com.mashjulal.the_debtor.database;

public class DebtsDBSchema {
    public static final class DebtTable {
        public static final String TABLE_NAME = "table_debts";

        public static final class Columns {
            public static final String
                    ID = "id",
                    TYPE = "type",
                    THING_NAME = "thing_type",
                    PHOTO_PATH = "photo_path",
                    MONEY_AMOUNT = "money_amount",
                    BORROW_DATE = "borrow_date",
                    RETURN_DATE = "return_date",
                    DESCRIPTION = "description",
                    RECIPIENT_NAME = "recipient_name",
                    RECIPIENT_PHOTO_PATH = "recipient_photo_path";
        }

    }
}
