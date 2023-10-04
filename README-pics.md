
This project illustrates five Java-based Selenium implementation approaches.  These are basic test models, emphasizing setup construction over testing logic.  While these models probably do not fit your exact requirements,  they hopefully provide fair bases to build on your own.  This project also illustrates a personal learning path. Working through this simple-to-moderately complex test model progression helped me re-introduce myself to Java-based Selenium testing while gaining background in Docker, Kubernetes and Helm. I hope it helps you as well.

**Testing Models**

  1. Local all-in-one -- `01-sel-local`.
  2. Remote all-in-one using Docker -- `02-sel-docker`.
  3. Docker-Compose Server & hub -- `03-sel-docker-cmp`.
  4. Kubernetes (K8s) Server & hub -- `04-sel-kubernetes`.
  5. Scalable K8s using Helm -- `05-sel-helm-keda`.
	

**Component Definitions -- Selenium Webdriver Architecture**

![Web Driver Architecture](https://hackr.io/blog/uploads/images/1570190913rXish5jdLA.jpg 'Web Driver Architecture')

*Credit: Saif Sadiq, hackr.io*

- **WebDriver** -- WebDriver drives a browser natively, as a user would, either locally or from a remote machine. Selenium WebDriver refers to both the language bindings and the implementations of the individual browser controlling code (e.g. ChromeDriver). This is commonly referred to as just WebDriver.
- **Browser** -- The Browser component provides capabilities and features specific to a given browser.  Browsers supported by WebDriver include Microsoft Edge, Firefox and Chrome.
- **Remote WebDriver** -- You can use WebDriver remotely the same way you would use it locally. The primary difference is that a remote WebDriver needs to be configured so that it can run your tests on a separate machine.
- **Grid** -- Selenium Grid allows the execution of WebDriver scripts on remote machines by routing commands sent by the client to remote browser instances.

**Note:** I find it helpful to think of the Selenium test and its supporting logic and systems defined above as separate from the Application under test (AUT).  We can also see that the our illustration makes no mention of the AUT, assuming the "real browser" interacts remotely with it to produce test results.

**Implementations**

The goal implementation and our most robust testing solution is *Approach 5: Remote with auto-scalable Pods (KEDA)*. Jumping straight into that model is a bit overwhelming; it's better to build up to it in smaller steps, gradually increasing the tool set capability and complexity. We will start simply with Java and Selenium, then add, Docker, Docker Compose, Kubernetes, and finally Kubernetes/KEDA.  You can follow along with each approach, each provided separately, so you can see exactly how each approach is extended to provide better capability in exchange for a bit more complexity.  Details of each approach follows.

**Approach 1: Local all-in-one**

![Basic Selenium](https://www.selenium.dev/images/documentation/webdriver/basic_comms.png)

*Credit, all illustrations: www.selenium.dev*

This is a basic selenium approach which places the tests, browser and driver on a local machine without using [Selenium Grid](https://www.selenium.dev/documentation/grid/ 'Selenium Grid Documentation') for remote testing. This approach is perhaps useful for initially defining code to test browser interactions, but it's limited to local-only test execution and requires the host to provide chrome and chrome driver code.  A perhaps "cleaner" variation with better control of the chrome and browser versions in the test execution path could be to build a "Selenium execution" docker container to run the tests.  The extra effort includes constructing the test container with browser, driver, supporting code libraries and test code.  Still, the tests would remain scale-constrained.  A simple demonstration of the hosted approach is found in the `01-sel-local` directory.  Maven provides the required libraries. Your host provides Chrome and Chromedriver.  You may have to download Chrome a and the chromedriver (See ***Note:** below).  Run your tests from this directory with the command, `./scripts/mavenCleanTest`.  

***Note:** Since the Selenium test logic seeks to match the version of chomedriver on your host's path with the Chrome (browser) version on the path, you my see warnings or errors with recommendations to update Chromedriver and/or Chrome.  If you don't have Chrome or the Chrome webdriver locally available, the [Selenium documentation](https://chromedriver.chromium.org/downloads 'ChromeDriver download') provides information on how to change or upgrade Chrome and ChromeDriver

**Test Source**

The example test used in all models is basic -- Navigate to a home page, verify its content, take a picture.  Here is the source used. You can find it in ./src/test/java/org/jr/selenium in each project:

![Test Source Code](https://github.com/jrusso1859/Scalable-Selenium/blob/main/Images/LocalTest.png)

**Approach 2: Remote all-in-one**

![Remote Selenium](https://github.com/jrusso1859/Scalable-Selenium/blob/main/Images/docker.png)

This approach executes the test separately from the browser interface, driver and browser functions, which are all containerized by Docker.  The example is found in  `02-sel-docker`.  The docker container exposes port 4444, allowing you to review component test status. It also contains VNC, a screen sharing application and exposes port 7900, allowing you to view container browser activity using the VNC-provided console.  While this example runs locally, you could just as easily run the containerized Docker image on its own host, connecting to it remotely from tests running locally.  While this starts us effectivly along the scalability path, it restricts us to scaling grid, driver and browser as one unit only.  Since Selenium Grid easily handles multple browser nodes, it should be scaled at a different rate than the browser nodes. Our following models provide and extend scaling with additional flexibility.

**Approach 3: Remote with Separate Containers**

![Remote Server](https://github.com/jrusso1859/Scalable-Selenium/blob/main/Images/docker-compose.png)

In this approach, we implement two docker containers, one for Selenium Grid and another for the Browser/Driver components.  Using this model, we could easily add additional browser containers if needed. We use Docker Compose at the command line to co-ordinate container starting and stopping.   AS stated earlier, Selenium Grid is able to handle multiple browser/drivers simultaneously.  This approach supports multiple browser/driver containers per grid.  With changes to the docker compose code, we can scale the grid and browser components at different rates. A simple demonstration of this approach is found in the `03-sel-docker-cmp` directory.  Start the two containers by executing `./scripts/hubAndNode`, then run your tests with `./scripts/mavenCleanTest`.

**Approach 4: Remote with Scalable Pods**

![Kubernetes Managed](https://github.com/jrusso1859/Scalable-Selenium/blob/main/Images/kube-1.png)


The above approach is technically scaleable because are agble to add Chrome, Firefox or MS Edge browser nodes with code changes.  However, those additions would require us to manually reconfigure docker-compose or issue additional docker-compose commands.  Kubernetes offers us a container options enabling us to orchestrate container scaling.  We can use Kubernetes to help us manage our re-sizing operations as testing demand fluctuates.

We use Kubernetes to orchestrate two pod sets: a selenium grid (or hub) pod and a set of two selenium Chrome pods. Kubernetes exposes the grid using a service definition, `selenium-hub-svc.yaml`. This allows us to connect to the hub in tests.  We can configure Kubernetes to automatically scale out Chrome pods as needed in high volume scenarios based on CPU and/or memory usage.  A simple demonstration of this approach is found in the `04-sel-kubernetes` directory. Execute `./scripts/selClusterUp` to initiate a minikube cluster running Selenium Hub and two Selenium Chrome nodes.  Then execute `./scripts/mavenCleanTest` to run your test.

**Problem: Browser Pod Scale-in**

By default, Kubernetes randomly selects pods to discard during scale-in operations, including pods which are running a tests.  We are close, but we need to add capability to better control automated scale-in.  See details in our next model. 

**Approach 5: Remote with Auto-scaleable Pods (KEDA)**

![Kubernetes Managed](https://github.com/jrusso1859/Scalable-Selenium/blob/main/Images/kubernetes-scaled.png)

 With [KEDA](https://keda.sh/ 'Keda home'), or **K**ubernetes-based **E**vent **D**riven **A**utoscaler, kubernetes can drive the scaling of browser containers in Kubernetes (up, or down) based on the number of events in test queues awaiting procesing.

Directory `05-sel-helm-keda` illustrates an example of an auto-scalable selenium test model.  We auto-scale the Chrome browser pods based on the number of selenium test requests waiting in the Selenium grid's queue. See the details of the KEDA architecture in [Keda Concepts](https://keda.sh/docs/2.11/concepts/ 'Kubernetes Documentation Site: KEDA Details.')


**References**

* [Selenium documentation (Github)] (https://github.com/SeleniumHQ/docker-selenium 'Selenium documentation')

* [Selenium 4.0 Components Reference](https://www.selenium.dev/documentation/overview/components/ 'Selnium Components')

* [Kubernetes Overview](https://kubernetes.io/docs/concepts/overview/ 'Kubernetes Summary')

* [Selenium Kubernetes Examples](https://github.com/kubernetes/examples/tree/master/staging/selenium 'Selenium Kubernetes Examples')

* [Scaling Grids](https://www.selenium.dev/blog/2022/scaling-grid-with-keda/ 'Blog sharing details of using KEDA to scale testing.')

* [Selenium 4 Grid Example](https://www.linkedin.com/pulse/selenium-4-grid-integration-kubernetes-rishi-khanna/)

* [Docker-implemented Selenium](https://github.com/SeleniumHQ/docker-selenium#deploying-to-kubernetes 'Docker selenium')

* [Scaling Docker-based Selenium](https://www.selenium.dev/blog/2022/scaling-grid-with-keda/ 'Using KEDA')


