#!/bin/bash
export NVM_DIR="/usr/local/nvm"
[ -s "$NVM_DIR/nvm.sh" ] && \. "$NVM_DIR/nvm.sh"
nvm use 20
export CHROME_BIN=/usr/bin/chromium

if [ ! -d "/home/coder/project/workspace/angularapp" ]; then
    cp -r /home/coder/project/workspace/karma/angularapp /home/coder/project/workspace/;
fi

if [ -d "/home/coder/project/workspace/angularapp" ]; then
    echo "project folder present"
    cp /home/coder/project/workspace/karma/karma.conf.js /home/coder/project/workspace/angularapp/karma.conf.js;
    if [ -e "/home/coder/project/workspace/angularapp/src/app/services/auth.service.ts" ]; then
        cp /home/coder/project/workspace/karma/auth.service.spec.ts /home/coder/project/workspace/angularapp/src/app/services/auth.service.spec.ts;
    else
        echo "Frontend_should_create_auth_service FAILED";
    fi

    if [ -e "/home/coder/project/workspace/angularapp/src/app/models/driver-request.model.ts" ]; then
        cp /home/coder/project/workspace/karma/driver-request.model.spec.ts /home/coder/project/workspace/angularapp/src/app/models/driver-request.model.spec.ts;
    else
        echo "Frontend_DriverRequest_model_should_create_an_instance_with_defined_properties FAILED";
    fi

    if [ -e "/home/coder/project/workspace/angularapp/src/app/services/driver-request.service.ts" ]; then
        cp /home/coder/project/workspace/karma/driver-request.service.spec.ts /home/coder/project/workspace/angularapp/src/app/services/driver-request.service.spec.ts;
    else
        echo "Frontend_should_create_driverRequest_service FAILED";
    fi

    if [ -e "/home/coder/project/workspace/angularapp/src/app/models/driver.model.ts" ]; then
        cp /home/coder/project/workspace/karma/driver.model.spec.ts /home/coder/project/workspace/angularapp/src/app/models/driver.model.spec.ts;
    else
        echo "Frontend_Driver_model_should_create_an_instance_with_defined_properties FAILED";
    fi

    if [ -e "/home/coder/project/workspace/angularapp/src/app/services/driver.service.ts" ]; then
        cp /home/coder/project/workspace/karma/driver.service.spec.ts /home/coder/project/workspace/angularapp/src/app/services/driver.service.spec.ts;
    else
        echo "Frontend_should_create_drive_service FAILED";
    fi

    if [ -e "/home/coder/project/workspace/angularapp/src/app/models/feedback.model.ts" ]; then
        cp /home/coder/project/workspace/karma/feedback.model.spec.ts /home/coder/project/workspace/angularapp/src/app/models/feedback.model.spec.ts;
    else
        echo "Frontend_Feedback_model_should_create_an_instance_with_defined_properties FAILED";
    fi

    if [ -e "/home/coder/project/workspace/angularapp/src/app/services/feedback.service.ts" ]; then
        cp /home/coder/project/workspace/karma/feedback.service.spec.ts /home/coder/project/workspace/angularapp/src/app/services/feedback.service.spec.ts;
    else
        echo "Frontend_should_create_feedback_service FAILED";
    fi

    if [ -e "/home/coder/project/workspace/angularapp/src/app/models/user.model.ts" ]; then
        cp /home/coder/project/workspace/karma/user.model.spec.ts /home/coder/project/workspace/angularapp/src/app/models/user.model.spec.ts;
    else
        echo "Frontend_User_model_should_create_an_instance FAILED";
    fi

    if [ -d "/home/coder/project/workspace/angularapp/node_modules" ]; then
        cd /home/coder/project/workspace/angularapp/
        npm test;
    else
        cd /home/coder/project/workspace/angularapp/
        yes | npm install
        npm test
    fi
else
    echo "Frontend_should_create_auth_service FAILED";
    echo "Frontend_DriverRequest_model_should_create_an_instance_with_defined_properties FAILED";
    echo "Frontend_should_create_driverRequest_service FAILED";
    echo "Frontend_Driver_model_should_create_an_instance_with_defined_properties FAILED";
    echo "Frontend_should_create_drive_service FAILED";
    echo "Frontend_Feedback_model_should_create_an_instance_with_defined_properties FAILED";
    echo "Frontend_should_create_feedback_service FAILED";
    echo "Frontend_User_model_should_create_an_instance FAILED";
fi
