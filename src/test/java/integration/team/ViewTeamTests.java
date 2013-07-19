package integration.team;

import integration.configuration.IntegrationTestsConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.bootstrap.context.initializer.ConfigFileApplicationContextInitializer;
import org.springframework.site.domain.team.MemberProfile;
import org.springframework.site.domain.team.TeamRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = IntegrationTestsConfiguration.class, initializers = ConfigFileApplicationContextInitializer.class)
@Transactional
public class ViewTeamTests {

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private TeamRepository teamRepository;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void getTeamPageOnlyShowsNotHiddenMembers() throws Exception {
		MemberProfile visible = new MemberProfile();
		visible.setName("First Last");
		visible.setGithubUsername("someguy");
		visible.setMemberId("someguy");

		teamRepository.save(visible);

		MemberProfile hidden = new MemberProfile();
		hidden.setName("Other dude");
		hidden.setGithubUsername("dude");
		hidden.setMemberId("dude");
		hidden.setHidden(true);

		teamRepository.save(hidden);

		this.mockMvc.perform(get("/about/team"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html"))
				.andExpect(content().string(containsString("First Last")))
				.andExpect(content().string(containsString("someguy")))
				.andExpect(content().string(not(containsString("dude"))));
	}

}