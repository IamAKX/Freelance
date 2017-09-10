var app = angular.module('freelance', [
    'ngSanitize',
    'angularModalService',
    'ngResource',
    'ngMaterial',
    'ui.router',
    'angularFileUpload',
    'angular-filepicker',
    'mdMarkdownIt',
    'ngClickCopy',
    'angularMoment',
    'youtube-embed'
])

app.controller('adminLoginCtrl', function($scope, $http, $mdDialog, $rootScope, toast) {
    $scope.user = {}
    $scope.submit = function() {
        $http.post('/api/user/admin/login', $scope.user).then(function(res) {
            if (res.data.length == 0) {
                $scope.errorMsg = "Invalid username or password";
            } else {
                $rootScope.currentUser = res.data;
                $mdDialog.cancel();
                toast.showSimpleToast("LOGGED IN AS ADMIN")
            }
        })
    }
})

app.controller('mainCtrl', function($scope, $http, $state, $mdDialog, $rootScope) {
    $scope.loginModal = function() {
        $mdDialog.show({
                controller: 'adminLoginCtrl',
                templateUrl: 'modal/adminLogin.html',
                parent: angular.element(document.body),
                clickOutsideToClose: true,
                locals: {
                    data: null
                }
            })
            .finally(function(result) {
                // getUploadedApp();
            });
    }

    $scope.logout = function() {
        $http.post('/api/user/admin/logout')
            .then(function() {
                $rootScope.currentUser = null;
                $state.go('home');
                toast.showSimpleToast('logged out');
            });
    }

})



app.config(function($stateProvider, $urlRouterProvider, filepickerProvider) {

    $urlRouterProvider.otherwise('/home');

    $stateProvider
        .state('home', {
            url: '/home',
            templateUrl: '/partials/home.html',
            controller: 'homeCtrl',
            resolve: {
                logincheck: checkUser
            }
        })
        .state('api', {
            url: '/api',
            templateUrl: '/partials/api.html',
            controller: 'apiCtrl',
            resolve: {
                logincheck: checkLogin
            }
        })
        .state('category', {
            url: '/category',
            templateUrl: '/partials/category.html',
            controller: 'constCtrl',
            resolve: {
                logincheck: checkLogin
            }
        })
        .state('doc', {
            url: '/doc',
            templateUrl: '/partials/doc.html',
            controller: 'docCtrl',
            resolve: {
                logincheck: checkUser
            }
        })
        .state('app', {
            url: '/app',
            templateUrl: '/partials/app.html',
            controller: 'appCtrl',
            resolve: {
                logincheck: checkUser
            }
        })
        .state('flowchart', {
            url: '/flowchart',
            templateUrl: '/partials/flowchart.html',
            resolve: {
                logincheck: checkUser
            }
        })
    filepickerProvider.setKey('AbjW5B0mTQdehxDWk6vzLz');
});

app.factory('toast', function($mdToast) {
    return {
        showSimpleToast: function(msg) {
            $mdToast.show(
                $mdToast.simple()
                .textContent(msg)
                .action('OK')
                .highlightAction(false)
            );
        }
    }
})


var checkLogin = function($q, $timeout, $http, $state, $location, $rootScope) {
    var deferred = $q.defer();
    $http.get('/api/user/admin/loggedin')
        .then(function(res) {
            $rootScope.errorMessage = null;
            if (res.data !== '0') {
                $rootScope.currentUser = res.data;
                deferred.resolve();
            } else {
                $rootScope.errorMessage = 'You are not logged in';
                deferred.reject();
                $state.go('home');
            }
        });
    return deferred.promise;
};

var checkUser = function($q, $timeout, $http, $state, $location, $rootScope) {
    var deferred = $q.defer();
    $http.get('/api/user/admin/loggedin')
        .then(function(res) {
            $rootScope.errorMessage = null;
            if (res.data !== '0') {
                $rootScope.currentUser = res.data;
                deferred.resolve();
            } else {
                $rootScope.currentUser = null;
                deferred.resolve();
            }
        });
    return deferred.promise;
};