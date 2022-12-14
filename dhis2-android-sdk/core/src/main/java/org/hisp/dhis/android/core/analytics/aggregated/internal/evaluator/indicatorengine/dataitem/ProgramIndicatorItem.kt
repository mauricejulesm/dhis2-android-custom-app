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
package org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator.indicatorengine.dataitem

import org.hisp.dhis.android.core.analytics.AnalyticsException
import org.hisp.dhis.android.core.analytics.aggregated.DimensionItem
import org.hisp.dhis.android.core.analytics.aggregated.MetadataItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.AnalyticsServiceEvaluationItem
import org.hisp.dhis.android.core.parser.internal.expression.CommonExpressionVisitor
import org.hisp.dhis.android.core.parser.internal.expression.ExpressionItem
import org.hisp.dhis.android.core.parser.internal.expression.ParserUtils
import org.hisp.dhis.android.core.program.ProgramIndicator
import org.hisp.dhis.parser.expression.antlr.ExpressionParser.ExprContext

internal class ProgramIndicatorItem : ExpressionItem {

    override fun evaluate(ctx: ExprContext, visitor: CommonExpressionVisitor): Any? {
        val programIndicator = getProgramIndicator(ctx, visitor)
        val dataItem = DimensionItem.DataItem.ProgramIndicatorItem(programIndicator.uid())
        val evaluationItem = getEvaluationItem(dataItem, visitor)
        val metadataEntry = getMetadataEntry(programIndicator)

        return visitor.indicatorContext.programIndicatorEvaluator.evaluate(
            evaluationItem = evaluationItem,
            metadata = visitor.indicatorContext.contextMetadata + metadataEntry
        ) ?: ParserUtils.DOUBLE_VALUE_IF_NULL
    }

    override fun getSql(ctx: ExprContext, visitor: CommonExpressionVisitor): Any? {
        val programIndicator = getProgramIndicator(ctx, visitor)
        val dataItem = DimensionItem.DataItem.ProgramIndicatorItem(programIndicator.uid())
        val evaluationItem = getEvaluationItem(dataItem, visitor)
        val metadataEntry = getMetadataEntry(programIndicator)

        return visitor.indicatorContext.programIndicatorEvaluator.getSql(
            evaluationItem = evaluationItem,
            metadata = visitor.indicatorContext.contextMetadata + metadataEntry
        )?.let { "($it)" }
    }

    private fun getProgramIndicator(ctx: ExprContext, visitor: CommonExpressionVisitor): ProgramIndicator {
        val programIndicatorId = ctx.uid0.text

        return programIndicatorId?.let {
            visitor.indicatorContext.programIndicatorRepository
                .withAnalyticsPeriodBoundaries()
                .uid(it)
                .blockingGet()
        } ?: throw AnalyticsException.InvalidProgramIndicator(programIndicatorId)
    }

    private fun getEvaluationItem(
        dataItem: DimensionItem.DataItem,
        visitor: CommonExpressionVisitor
    ): AnalyticsServiceEvaluationItem {
        return AnalyticsServiceEvaluationItem(
            dimensionItems = listOf(dataItem),
            filters = visitor.indicatorContext.evaluationItem.filters +
                visitor.indicatorContext.evaluationItem.dimensionItems.map { it as DimensionItem }
        )
    }

    private fun getMetadataEntry(programIndicator: ProgramIndicator): Pair<String, MetadataItem> {
        return programIndicator.uid() to MetadataItem.ProgramIndicatorItem(programIndicator)
    }
}
