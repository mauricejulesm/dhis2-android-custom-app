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

package org.hisp.dhis.android.core.trackedentity.search;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor;
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent;
import org.hisp.dhis.android.core.systeminfo.DHISVersion;
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceService;

import java.text.ParseException;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import dagger.Reusable;
import retrofit2.Call;

@SuppressWarnings({"PMD.PreserveStackTrace"})
@Reusable
class TrackedEntityInstanceQueryCallFactory {

    private final TrackedEntityInstanceService service;
    private final SearchGridMapper mapper;
    private final APICallExecutor apiCallExecutor;
    private final DHISVersionManager dhisVersionManager;

    @Inject
    TrackedEntityInstanceQueryCallFactory(
            @NonNull TrackedEntityInstanceService service,
            @NonNull SearchGridMapper mapper,
            APICallExecutor apiCallExecutor,
            DHISVersionManager dhisVersionManager) {
        this.service = service;
        this.mapper = mapper;
        this.apiCallExecutor = apiCallExecutor;
        this.dhisVersionManager = dhisVersionManager;
    }

    Callable<List<TrackedEntityInstance>> getCall(final TrackedEntityInstanceQueryOnline query) {
        return () -> queryTrackedEntityInstances(query);
    }

    @SuppressWarnings({"PMD.NPathComplexity"})
    private List<TrackedEntityInstance> queryTrackedEntityInstances(TrackedEntityInstanceQueryOnline query)
            throws D2Error {

        String orgUnitModeStr = query.orgUnitMode() == null ? null : query.orgUnitMode().toString();

        String assignedUserModeStr = query.assignedUserMode() == null ? null : query.assignedUserMode().toString();
        String enrollmentStatus = query.enrollmentStatus() == null ? null : query.enrollmentStatus().toString();
        String eventStatus = getEventStatus(query);
        String orgUnits = query.orgUnits().isEmpty() ? null :
                CollectionsHelper.joinCollectionWithSeparator(query.orgUnits(), ";");

        Call<SearchGrid> searchGridCall = service.query(orgUnits, orgUnitModeStr, query.program(),
                query.formattedProgramStartDate(), query.formattedProgramEndDate(), enrollmentStatus, query.followUp(),
                query.formattedEventStartDate(), query.formattedEventEndDate(), eventStatus,
                query.trackedEntityType(), query.query(), query.attribute(), query.filter(), assignedUserModeStr,
                query.order(), query.paging(), query.page(), query.pageSize());

        try {
            SearchGrid searchGrid = apiCallExecutor.executeObjectCallWithErrorCatcher(searchGridCall,
                    new TrackedEntityInstanceQueryErrorCatcher());

            return mapper.transform(searchGrid);
        } catch (ParseException pe) {
            throw D2Error.builder()
                    .errorCode(D2ErrorCode.SEARCH_GRID_PARSE)
                    .errorComponent(D2ErrorComponent.SDK)
                    .errorDescription("Search Grid mapping exception")
                    .originalException(pe)
                    .build();
        }
    }

    private String getEventStatus(TrackedEntityInstanceQueryOnline query) {
        if (query.eventStatus() == null) {
            return null;
        } else if (!dhisVersionManager.isGreaterThan(DHISVersion.V2_33) &&
                query.eventStatus().equals(EventStatus.ACTIVE)) {
            return EventStatus.VISITED.toString();
        } else {
            return query.eventStatus().toString();
        }
    }
}
