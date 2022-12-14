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

package org.hisp.dhis.android.core.trackedentity.internal;

import org.hisp.dhis.android.core.arch.api.fields.internal.Field;
import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.fields.internal.FieldsHelper;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.FilterPeriod;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceEventFilter;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceFilter;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceFilterTableInfo.Columns;

public final class TrackedEntityInstanceFilterFields {

    public final static String ENROLLMENT_CREATED_PERIOD = "enrollmentCreatedPeriod";
    public final static String FOLLOW_UP = "followup";
    public final static String EVENT_FILTERS = "eventFilters";

    private static final FieldsHelper<TrackedEntityInstanceFilter> fh = new FieldsHelper<>();

    public static final Field<TrackedEntityInstanceFilter, String> programUid =
            Field.create(Columns.PROGRAM + "." + BaseIdentifiableObject.UID);

    public static final Fields<TrackedEntityInstanceFilter> allFields = Fields.<TrackedEntityInstanceFilter>builder()
            .fields(fh.getIdentifiableFields())
            .fields(
                    fh.nestedFieldWithUid(Columns.PROGRAM),
                    fh.<String>field(Columns.DESCRIPTION),
                    fh.<Integer>field(Columns.SORT_ORDER),
                    fh.<EnrollmentStatus>field(Columns.ENROLLMENT_STATUS),
                    fh.<Boolean>field(FOLLOW_UP),
                    fh.<FilterPeriod>field(ENROLLMENT_CREATED_PERIOD),
                    fh.<TrackedEntityInstanceEventFilter>nestedField(EVENT_FILTERS)
                            .with(TrackedEntityInstanceEventFilterFields.allFields)
            ).build();

    private TrackedEntityInstanceFilterFields() {
    }
}