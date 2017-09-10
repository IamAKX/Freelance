app.controller('saveModalCtrl', function($scope, $mdDialog, $mdToast, $http) {
    $scope.api = {};
    $scope.close = function() {
        $mdDialog.hide();
    };
    $scope.saveNew = function(api) {
        $http.post('/api/apiData/saveApi', api).then(function(data) {
            $mdDialog.hide();
            $scope.showSimpleToast("api added");

        });
    };
    $scope.showSimpleToast = function(msg) {
        $mdToast.show(
            $mdToast.simple()
            .textContent(msg)
            .action('OK')
            .highlightAction(false)
        );
    };
});