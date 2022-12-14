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

package org.hisp.dhis.android.core.parser.internal.service.dataitem;

import org.hisp.dhis.android.core.constant.Constant;
import org.hisp.dhis.android.core.parser.internal.expression.CommonExpressionVisitor;
import org.hisp.dhis.android.core.parser.internal.expression.ExpressionItem;
import org.hisp.dhis.antlr.ParserExceptionWithoutContext;

import static org.hisp.dhis.android.core.parser.internal.expression.ParserUtils.DOUBLE_VALUE_IF_NULL;
import static org.hisp.dhis.parser.expression.antlr.ExpressionParser.ExprContext;

/**
 * Expression item Constant
 *
 * @author Jim Grace
 */
public class ItemConstant implements ExpressionItem {

    @Override
    public Object getDescription(ExprContext ctx, CommonExpressionVisitor visitor) {
        Constant constant = visitor.getConstantMap().get(ctx.uid0.getText());

        if (constant == null) {
            throw new ParserExceptionWithoutContext("No constant defined for " + ctx.uid0.getText());
        }

        visitor.getItemDescriptions().put(ctx.getText(), constant.displayName());

        return DOUBLE_VALUE_IF_NULL;
    }

    @Override
    public Object getItemId(ExprContext ctx, CommonExpressionVisitor visitor) {
        visitor.getItemIds().add(getDimensionalItemId(ctx));

        return DOUBLE_VALUE_IF_NULL;
    }

    @Override
    public Object evaluate(ExprContext ctx, CommonExpressionVisitor visitor) {
        Constant constant = visitor.getConstantMap().get(ctx.uid0.getText());

        if (constant == null) {
            throw new ParserExceptionWithoutContext("Can't find constant to evaluate " + ctx.uid0.getText());
        }

        return constant.value();
    }

    @Override
    public final Object regenerate(ExprContext ctx, CommonExpressionVisitor visitor) {
        Constant constant = visitor.getConstantMap().get(ctx.uid0.getText());

        if (constant == null || constant.value() == null) {
            return ctx.getText();
        } else {
            return constant.value().toString();
        }
    }

    @Override
    public Object getSql(ExprContext ctx, CommonExpressionVisitor visitor) {
        return evaluate(ctx, visitor);
    }

    private DimensionalItemId getDimensionalItemId(ExprContext ctx) {
        return DimensionalItemId.builder()
                .dimensionalItemType(DimensionalItemType.CONSTANT)
                .id0(ctx.uid0.getText())
                .build();
    }
}
