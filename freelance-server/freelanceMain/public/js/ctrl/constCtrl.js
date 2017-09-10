app.controller('constCtrl', function($scope, $http, $mdDialog, toast) {
    $scope.selectedUserId = null;

    function init() {
        getAllCats();
        getAllUsers();
        getCats();
        getAllProjects();
        getAllHire();
        getAllReports();
    }

    function getAllUsers() {
        $http.get('/api/admin/alluser').then(function(res) {
            $scope.users = res.data;
            console.log(res.data);
        })
    }

    /**
     * get the list of categories
     */
    function getAllCats() {
        $http.get('/api/constant/getallCategory').then(function(res) {
            $scope.cats = res.data;
        })
    }

    /**
     * add a new category
     */
    $scope.addNewModal = function() {
        $mdDialog.show({
                controller: 'catModalCtrl',
                templateUrl: 'modal/categoryModal.html',
                parent: angular.element(document.body),
                clickOutsideToClose: true,
                locals: {
                    data: null
                }
            })
            .finally(function(result) {
                init();
            });
    };

    /**
     * edit a category
     */
    $scope.editModal = function(data) {
        $mdDialog.show({
                controller: 'catModalCtrl',
                templateUrl: 'modal/categoryModal.html',
                parent: angular.element(document.body),
                clickOutsideToClose: true,
                locals: {
                    data: data
                }
            })
            .finally(function(result) {
                init();
            });
    };

    /**
     * delete a category
     */
    $scope.deleteCat = function(data, index) {
        $http.post('/api/constant/deleteCategory', data).then(function(data) {
            toast.showSimpleToast("deleted");
            $scope.cats.splice(index, 1);
        })
    };

    //get list of all categories
    function getCats() {
        $http.get('/api/constant/getallCategory').then(function(res) {
            $scope.categoriesUrl = res.data;
        })
    }

    ////selected user

    $scope.getImgCategory = function(cat) {
        var url = _.find($scope.categoriesUrl, function(c) {
            return c.name == cat;
        });
        // console.log("====", url);
        if (url == undefined) {
            return 'http://www.theantway.com/wp-content/uploads/2012/04/blank_image.png';
        }
        return url.url;
    };

    /**
     * on selceting a user show his/her details
     */
    $scope.selectUser = function(userId) {
        $scope.selectedUserId = userId;
        var payload = { userId: $scope.selectedUserId };
        $http.post('/api/admin/getUserDetails', payload).then(function(res) {
            $scope.userData = res.data;
            $scope.userCategory = res.data.category[0].substring(1, res.data.category[0].length - 1).split(",");
            console.log(typeof(res.data.category[0]), $scope.userCategory);
            $http.post('/api/admin/getAllProjectsUser', payload).then(function(res) {
                $scope.projData = res.data.projects;
            })
        })
    };

    /**
     * toggle control over user
     */
    $scope.toggleData = function(param) {
        var payload = {
            userId: $scope.selectedUserId,
            param: param,
            value: $scope.userData[param]
        };
        $http.post('/api/admin/changeUserData', payload).then(function(res) {
            toast.showSimpleToast("changed");
        })
    };

    /**
     * permanent delete of user account
     */
    $scope.deleteUser = function() {
        $http.post('/user/deleteUser', { userId: $scope.selectedUserId }).then(function(data) {
            toast.showSimpleToast("delete user");
        })
    };

    ////selected user

    /**
     * get list of all posted projects
     * 
     */
    function getAllProjects() {
        $http.get('/api/admin/getAllProjects').then(function(res) {
            $scope.allProjects = res.data;
        })
    }

    /**
     * get a project information
     */
    $scope.getProjectInfo = function(pId) {
        $http.post('/api/admin/getProjectDetails', { pId: pId }).then(function(res) {
            console.log(res.data);
            $mdDialog.show({
                    controller: 'projDetail',
                    templateUrl: 'modal/projDescription.html',
                    parent: angular.element(document.body),
                    clickOutsideToClose: true,
                    locals: {
                        data: res.data
                    }
                })
                .finally(function(result) {
                    init();
                });

        })
    }


    ///// hire 

    /**
     * get all hire records
     */
    function getAllHire() {
        $http.get('/api/admin/allHireData').then(function(res) {
            $scope.hireData = res.data;
        })
    }

    function getAllReports() {
        $http.get('/api/admin/getAllReports').then(function(res) {
            $scope.reportData = res.data;
        })
    }


    init();

})

app.controller('projDetail', function($scope, $mdDialog, data) {
    $scope.projData = data;
})