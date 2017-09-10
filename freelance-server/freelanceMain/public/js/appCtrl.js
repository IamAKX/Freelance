app.controller('appCtrl', function($scope, filepickerService, $mdDialog, toast, $http) {

    $scope.upload = function() {
        filepickerService.pick({
                // mimetype: 'image/*',
                language: 'en',
                services: ['COMPUTER', 'DROPBOX', 'GOOGLE_DRIVE', 'IMAGE_SEARCH', 'FACEBOOK', 'INSTAGRAM'],
                openTo: 'IMAGE_SEARCH'
            },
            function(file) {
                console.log(file);
                if (file.mimetype == "application/vnd.android.package-archive") {

                    var fileUpload = {
                        name: file.filename,
                        size: file.size,
                        link: file.url,
                        state: "Idle"
                    }
                    console.log(fileUpload);
                    $http.post('/api/app/uploadApp', fileUpload).then(function(data) {
                        getUploadedApp();
                    })
                } else {
                    toast.showSimpleToast("please upload an apk file");
                }

            }
        );
    };

    function getUploadedApp() {
        $http.get('/api/app/getUploadedApp').then(function(data) {
            $scope.apps = data.data;
        })
    }

    $scope.deleteApp = function(id) {
        $http.post('/api/app/deleteApp', { appId: id }).then(function(data) {
            console.log(data.data);
        })
    }

    $scope.update = function(appData) {
        $mdDialog.show({
                controller: 'appEditCtrl',
                templateUrl: 'modal/appEditModal.html',
                parent: angular.element(document.body),
                clickOutsideToClose: true,
                locals: {
                    data: appData
                }
            })
            .finally(function(result) {
                getUploadedApp();
            });
    }

    getUploadedApp();
})