/*
 * Copyright 2020 HM Revenue & Customs
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

package controllers

import controllers.actions.Actions
import forms.NameFormProvider
import javax.inject.Inject
import navigation.Navigator
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import models.{Name, NormalMode}
import pages.NamePage
import repositories.SessionRepository
import views.html.NameView

import scala.concurrent.{ExecutionContext, Future}

class NameController @Inject()(
                                val controllerComponents: MessagesControllerComponents,
                                actions: Actions,
                                formProvider: NameFormProvider,
                                sessionRepository: SessionRepository,
                                view: NameView,
                                navigator: Navigator
                              )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form = formProvider.withPrefix("deceasedSettlor.name")

  def onPageLoad(): Action[AnyContent] = actions.authWithData {
    implicit request =>

      val preparedForm = request.userAnswers.get(NamePage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm))
  }

  def onSubmit(): Action[AnyContent] = actions.authWithData.async {
    implicit request =>
      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors))),
        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(NamePage, value))
            _ <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(NamePage, NormalMode, updatedAnswers))
        }
      )
  }
}