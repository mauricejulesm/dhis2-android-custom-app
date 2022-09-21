/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.maintenance;

import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.DbDateColumnAdapter;
import org.hisp.dhis.android.core.common.BaseObject;

import java.util.Date;

@AutoValue
public abstract class ForeignKeyViolation extends BaseObject {

    @Nullable
    public abstract String fromTable();

    @Nullable
    public abstract String fromColumn();

    @Nullable
    public abstract String toTable();

    @Nullable
    public abstract String toColumn();

    @Nullable
    public abstract String notFoundValue();

    @Nullable
    public abstract String fromObjectUid();

    @Nullable
    public abstract String fromObjectRow();

    @Nullable
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date created();

    @NonNull
    public static ForeignKeyViolation create(Cursor cursor) {
        return AutoValue_ForeignKeyViolation.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new AutoValue_ForeignKeyViolation.Builder();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public static abstract class Builder extends BaseObject.Builder<Builder> {

        public abstract Builder fromTable(String fromTable);

        public abstract Builder fromColumn(String fromColumn);

        public abstract Builder toTable(String toTable);

        public abstract Builder toColumn(String toColumn);

        public abstract Builder notFoundValue(String notFoundValue);

        public abstract Builder fromObjectUid(String fromObjectUid);

        public abstract Builder fromObjectRow(String fromObjectRow);

        public abstract Builder created(Date created);

        public abstract ForeignKeyViolation build();
    }
}