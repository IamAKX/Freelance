app.controller('catModalCtrl', function($scope, $http, $mdDialog, toast, data) {
    $scope.close = function() {
        $mdDialog.hide();
    }

    if (data == null) {
        $scope.catData = {
            name: '',
            url: ''
        };

    } else {
        $scope.catData = data;
    }
    $scope.saveCat = function() {
        console.log($scope.catData);
        if (data == null) {
            $http.post('/api/constant/setCategory', $scope.catData).then(function(res) {
                toast.showSimpleToast("categry saved");
                $scope.name = '';
                $scope.url = '';
            })
        } else {
            $http.post('/api/constant/updateallCategory', $scope.catData).then(function(res) {
                toast.showSimpleToast("category updated");
            })
        }
    }

    $scope.preview = function() {
        $scope.imageUrl = $scope.catData.url;
        console.log($cope.imageUrl);
    }


})