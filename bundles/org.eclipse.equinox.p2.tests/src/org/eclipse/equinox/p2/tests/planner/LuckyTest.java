package org.eclipse.equinox.p2.tests.planner;

import org.eclipse.equinox.internal.p2.director.ProfileChangeRequest;
import org.eclipse.equinox.p2.engine.*;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.planner.IPlanner;
import org.eclipse.equinox.p2.tests.*;

public class LuckyTest extends AbstractProvisioningTest {
	@IUDescription(content = "package: sdk \n" + "singleton: true\n" + "version: 1 \n" + "depends: platform = 1")
	public IInstallableUnit sdk1;

	@IUDescription(content = "package: platform \n" + "singleton: true\n" + "version: 1 \n")
	public IInstallableUnit platform1;

	@IUDescription(content = "package: sdk \n" + "singleton: true\n" + "version: 2 \n" + "depends: platform = 2")
	public IInstallableUnit sdk2;

	@IUDescription(content = "package: platform \n" + "singleton: true\n" + "version: 2 \n")
	public IInstallableUnit platform2;

	IProfile profile = createProfile("TestProfile." + getName());

	private IPlanner planner;

	private IEngine engine;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		IULoader.loadIUs(this);
		createTestMetdataRepository(new IInstallableUnit[] {sdk1, platform1, sdk2, platform2});
		System.out.println(sdk1);
		System.out.println(platform1);
		System.out.println(sdk2);
		System.out.println(platform2);
		planner = createPlanner();
		engine = createEngine();
		assertOK(install(profile, new IInstallableUnit[] {sdk1}, true, planner, engine));
	}

	public void testInstallSDK2() {
		assertNotOK(install(profile, new IInstallableUnit[] {platform2}, true, planner, engine));
		ProfileChangeRequest request = new ProfileChangeRequest(profile);
		request.add(platform2);
		ProfileChangeRequest res = new LuckyHelper().computeProfileChangeRequest(profile, planner, request, new ProvisioningContext(getAgent()), getMonitor());
		assertTrue(res.getAdditions().contains(sdk2));
		assertTrue(res.getRemovals().contains(sdk1));
	}

}