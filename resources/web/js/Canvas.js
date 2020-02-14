var mainContent = $("#mainCanvas")[0].getContext("2d");
var selfContent = $("#selfCanvas")[0].getContext("2d");
var coverContent = $("coverCanvas")[0].getContext("2d");

var selectId;
var locations = [];
var fLoc = [];
var eLoc = [];

var width = 1280;
var height = 680;

function mergeToMainCanvas(context) {
    var mainImageData = mainContent.getImageData(0, 0, width, height);
    var src = context.getImageData(0, 0, width, height).data;
    var main = mainImageData.data;
    var srcA, mainA, len = main.length;
    var srcR, srcG, srcB, mainR, mainG, mainB, _mainA;
    var demultiply;

    for (var px = 0; px < len; px+=4){
        srcA = src[px+3];
        mainA = main[px+3];
        _mainA = (srcA + mainA - (srcA * mainA)/255);
        
        srcR = src[px] * srcA;
        srcG = src[px+1] * srcA;
        srcB = src[px+2] * srcA;
        mainR = main[px] * mainA;
        mainG = main[px+1] * mainA;
        mainB = main[px+2] * mainA;

        main[px] = (srcR + mainG - (srcR * mainA)/255)/_mainA;
        main[px+1] = (srcG + mainG - (srcG * mainA)/255)/_mainA;
        main[px+2] = (srcB + mainB - (srcB * mainA)/255)/_mainA;
        main[px+3] = _mainA;
    }
    mainContent.putImageData(mainImageData, 0, 0)
}

function clearC() {

}