package com.zenith.activity

/**
 * Created by Admin on 26-02-2018.
 */
import android.app.LoaderManager
import android.content.Loader
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.widget.AdapterView;

class ContactsFragment : LoaderManager.LoaderCallbacks<Cursor> {
    override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<Cursor> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLoaderReset(p0: Loader<Cursor>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLoadFinished(p0: Loader<Cursor>?, p1: Cursor?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}