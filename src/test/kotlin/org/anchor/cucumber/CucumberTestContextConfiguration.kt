package org.anchor.cucumber

import io.cucumber.spring.CucumberContextConfiguration
import org.anchor.IntegrationTest
import org.springframework.test.context.web.WebAppConfiguration

@CucumberContextConfiguration
@IntegrationTest
@WebAppConfiguration
class CucumberTestContextConfiguration
