# Location of our cypress tests

Right now, the actual cypress tests reside in the [metasfresh-webui-frontend](https://github.com/metasfresh/metasfresh-webui-frontend) repo. This is going to change as of issue [#7](https://github.com/metasfresh/metasfresh-e2e/issues/7) Move existing cypress tests into this repo

# Build the docker image

To build the docker image locally, you can do

```
# this gets the most recent code; if you want to develop, it makes more sense to instead clone the whole repo
git clone -b master --depth=1 https://github.com/metasfresh/metasfresh-webui-frontend.git cypress-git-repo

# build the docker image
docker build --tag metasfresh-e2e --build-arg CACHEBUST=$(date "+%Y-%m-%d") .
```

# Run the docker image

run the docker container, e.g. like this

```
hostname=dev289.metasfresh.com
docker run --rm\
 -e "FRONTEND_URL=https://${hostname}:443"\
 -e "API_URL=https://${hostname}:443/rest/api"\
 -e "WS_URL=https://${hostname}:443/stomp"\
 -e "USERNAME=dev"\
 -e "PASSWORD=password"\
 -e "RECORD_KEY=NOT_SET"\
 -e "BROWSER=chrome"\
 -e "DEBUG_CYPRESS_OUTPUT=n"\
 -e "DEBUG_PRINT_BASH_CMDS=n"\
 -e "DEBUG_SLEEP_AFTER_FAIL=y"\
 metasfresh-e2e
```

# Develop

## Prequisites

Note that you might need to first install cypress; [this documentation](https://docs.cypress.io/guides/getting-started/installing-cypress.html#npm-install) tells you how:

> npm install cypress --save-dev

Also note that in addition you might also need to do a full `npm install` afterwards.

Also, you'll need to configure the login credentials/API endpoints. One file is responsible for this:

- `cypress/config.js` - stores API endpoints and login credentials

There is a file `cypress/config.js_template` which you can copy to `cypress/config.js` and edit according to your needs.

## Running

To run the tests, navigate to this repository's root folder type this in the terminal:

> npm run cypress:open

If the webui you test against is not running on http://localhost:3000 you can start cypress with the `CYPRESS_baseUrl` vairable (see examples).

When it runs, you can select particular test suites, or the whole suite to run. 

## Examples:

For my local minikube (with webui-api running in my IDE)

...edit the `cypress/config.js` like this:

```json
module.exports = {
  API_URL: 'http://localhost:8080/rest/api',
  PLUGIN_API_URL: 'http://localhost:9192/',
  WS_URL: 'http://localhost:8080/stomp',
  username: 'metasfresh',
  password: '<your-pw>',
};
```

...and start cypress like this:

> CYPRESS_baseUrl=http://localhost:30080 npm run cypress:open

For a dev-instance (dev133 in this example)

...edit the `cypress/config.js` like this:

```json
module.exports = {
  API_URL: 'https://dev133.metasfresh.com/rest/api/',
  WS_URL: 'https://dev133.metasfresh.com/stomp',
  PLUGIN_API_URL: 'http://localhost:9192/',
  username: 'dev',
  password: '<your-pw>',
};

...and start cypress like this:

```

> CYPRESS_baseUrl=https://dev133.metasfresh.com:443 npm run cypress:open

## Setting up Visual Studio Code

We currently use [VS code](https://code.visualstudio.com/download) to develop cypress tests.
Once you installed it, you can use IntelliSense for auto completion. 
Details here: https://docs.cypress.io/guides/tooling/intelligent-code-completion.html#Set-up-in-your-Dev-Environment-1 (Note: in the VS Code preferences, look for "edit settings.json").
