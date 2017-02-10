package stark.a.is.zhang.criminalintentapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import stark.a.is.zhang.criminalintentapp.database.CrimeBaseHelper;
import stark.a.is.zhang.criminalintentapp.database.CrimeCursorWrapper;
import stark.a.is.zhang.criminalintentapp.database.CrimeDbSchema.CrimeTable;

public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private SQLiteDatabase mDataBase;
    private Context mContext;

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }

        return sCrimeLab;
    }

    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        mDataBase = new CrimeBaseHelper(context.getApplicationContext())
                .getWritableDatabase();
    }

    public List<Crime> getCrimes() {
        List<Crime> crimes = new ArrayList<>();

        CrimeCursorWrapper cursor = queryCrimes(null, null);
        try {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return crimes;
    }

    public Crime getCrime(UUID id) {
        CrimeCursorWrapper cursor = queryCrimes(
                CrimeTable.Cols.UUID + " = ?",
                new String[] {id.toString()});

        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCrime();
        } finally {
            cursor.close();
        }
    }

    public void addCrime(Crime c) {
        ContentValues values = getContentValues(c);
        mDataBase.insert(CrimeTable.NAME, null, values);
    }

    public void updateCrime(Crime c) {
        String uuidString = c.getId().toString();
        ContentValues values = getContentValues(c);

        mDataBase.update(CrimeTable.NAME, values,
                CrimeTable.Cols.UUID + " = ?",
                new String[] {uuidString});
    }

    public void removeCrime(Crime c) {
        mDataBase.delete(CrimeTable.NAME,
                CrimeTable.Cols.UUID + " = ?",
                new String[] {c.getId().toString()});
        removePhotoFile(c);
    }

    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();

        values.put(CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());
        values.put(CrimeTable.Cols.CONTACT_ID, crime.getContactId());

        return values;
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDataBase.query(
                CrimeTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null);
        return new CrimeCursorWrapper(cursor);
    }

    public File getPhotoFile(Crime crime) {
        File externalFilesDir = mContext
                .getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (externalFilesDir == null) {
            return null;
        }

        return new File(externalFilesDir, crime.getPhotoFilename());
    }

    private void removePhotoFile(Crime crime) {
        File externalFilesDir = mContext
                .getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (externalFilesDir != null) {
            File file = new File(externalFilesDir, crime.getPhotoFilename());
            if (file.exists() && file.isFile()) {
                file.delete();
            }
        }
    }
}
