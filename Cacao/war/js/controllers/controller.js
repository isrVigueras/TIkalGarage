var app = angular.module("app", ['ngRoute']);
app.config(['$routeProvider',function($routeProvider) {


  $routeProvider.when('/empresas', {
    templateUrl: "pages/empresas.html",
    controller: "empresasController"
  });

  $routeProvider.when('/esquemas', {
    templateUrl: "pages/esquemas.html",
    controller: "esquemascontroller"
  });

  $routeProvider.when('/empleados', {
    templateUrl: "pages/empleados.html",
    controller: "empleadoscontroller"
  });

  $routeProvider.otherwise({
    redirectTo: 'index.html'
  });   

}]);
