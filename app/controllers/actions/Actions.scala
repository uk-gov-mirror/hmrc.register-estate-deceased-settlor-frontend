/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers.actions

import com.google.inject.Inject
import models.requests.{DataRequest, NameRequest, OptionalDataRequest}
import play.api.mvc.{ActionBuilder, AnyContent}

class Actions @Inject()(
                         identify: IdentifierAction,
                         getData: DataRetrievalAction,
                         requireData: DataRequiredAction,
                         nameRequiredAction: NameRequiredAction
                       ) {

  def authWithSession: ActionBuilder[OptionalDataRequest, AnyContent] =
    identify andThen getData

  def authWithData: ActionBuilder[DataRequest, AnyContent] =
    authWithSession andThen requireData

  def authWithName: ActionBuilder[NameRequest, AnyContent] =
    authWithSession andThen requireData andThen nameRequiredAction

}
