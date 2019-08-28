import obelisk from 'obelisk.js';
import {inject} from 'aurelia-framework';

interface BoardProperties {
  units: number,
  size: number
}


@inject(obelisk)
export class App {
  battleshipCanvas;
  pixelView;
  boardProperties : BoardProperties;

  constructor(private obelisk:obelisk){
    this.boardProperties = {
      'units':5,
      'size':60
    }
  }

  attached(){
    let canvas = this.battleshipCanvas;
    if(canvas){
      let point = new obelisk.Point(300,100);
      //the center of the screen
      this.pixelView = new obelisk.PixelView(canvas, point);
      this.dropCube(0,0,90,0x008000);

    }
  }

  dropCube(cubesX: number,cubesY:number,cubesZ:number,color:number){
    this.pixelView.clear();
    this.makeGrid();
    let dimensionCube = new obelisk.CubeDimension(60, 60, 60);
    let cubeColor = new obelisk.CubeColor().getByHorizontalColor(color);
    let cube = new obelisk.Cube(dimensionCube, cubeColor);
    this.pixelView.renderObject(cube, new obelisk.Point3D(cubesX, cubesY, cubesZ,color));
    
    if(cubesZ>0){
      cubesZ-=10;
      requestAnimationFrame(() => this.dropCube(cubesX,cubesY,cubesZ,color));
    }
  }


  makeGrid(){
      // Let's build a grid...  
    // Setup lines
    let lineColor = new obelisk.LineColor();
    let dimensionX = new obelisk.LineXDimension(this.boardProperties.units * this.boardProperties.size);
    let dimensionY = new obelisk.LineYDimension(this.boardProperties.units * this.boardProperties.size);
    let lineX = new obelisk.LineX(dimensionX, lineColor);
    let lineY = new obelisk.LineY(dimensionY, lineColor);

      // Create Grid:
      for (let x = 0; x < this.boardProperties.units + 1; x++) {
        this.pixelView.renderObject(lineX, new obelisk.Point3D(0, x * this.boardProperties.size, 0));
      }
      for (let y = 0; y < this.boardProperties.units + 1; y++) {
        this.pixelView.renderObject(lineY, new obelisk.Point3D(y * this.boardProperties.size, 0, 0));
      }
  }

}
