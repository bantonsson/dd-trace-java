/*
 * Copyright 2020, OpenTelemetry Authors
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


import datadog.trace.agent.test.AgentTestRunner
import listener.Config
import org.apache.activemq.junit.EmbeddedActiveMQBroker
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.jms.core.JmsTemplate
import spock.lang.Retry

import javax.jms.ConnectionFactory

import static JMS1Test.consumerTrace
import static JMS1Test.producerTrace

@Retry
class SpringListenerJMS1Test extends AgentTestRunner {

  def "receiving message in spring listener generates spans"() {
    setup:
    def context = new AnnotationConfigApplicationContext(Config)
    def factory = context.getBean(ConnectionFactory)
    def template = new JmsTemplate(factory)
    template.convertAndSend("SpringListenerJMS1", "a message")

    expect:
    assertTraces(2) {
      producerTrace(it, "Queue SpringListenerJMS1")
      consumerTrace(it, "Queue SpringListenerJMS1", trace(0)[0])
    }

    cleanup:
    context.getBean(EmbeddedActiveMQBroker).stop()
  }
}
