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

package connectors

import config.FrontendAppConfig
import javax.inject.Inject
import models.DeceasedSettlor
import play.api.libs.json.{JsSuccess, JsValue, Json}
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

class EstatesConnector @Inject()(http: HttpClient, config: FrontendAppConfig) {

  private lazy val deceasedUrl = s"${config.estatesUrl}/estates/deceased"

  def getDeceased()(implicit hc: HeaderCarrier, ec : ExecutionContext): Future[Option[DeceasedSettlor]] = {
    http.GET[JsValue](deceasedUrl) flatMap {
      _.validate[DeceasedSettlor] match {
        case JsSuccess(value, _) => Future.successful(Some(value))
        case _ => Future.successful(None)
      }
    }
  }

  def setDeceased(deceasedSettlor: DeceasedSettlor)(implicit hc: HeaderCarrier, ec : ExecutionContext): Future[HttpResponse] = {
    http.POST[JsValue, HttpResponse](deceasedUrl, Json.toJson(deceasedSettlor))
  }

  private lazy val resetTaxLiabilityUrl = s"${config.estatesUrl}/estates/reset-tax-liability"

  def resetTaxLiability()(implicit hc: HeaderCarrier, ec : ExecutionContext): Future[HttpResponse] = {
    http.POSTEmpty[HttpResponse](resetTaxLiabilityUrl)
  }
}
