export class GameParams {

  constructor(private  _allowedShips: number, private  _gameboardDimensions: number, private  _gamecellPixelSize: number){
  }

  get allowedShips(): number{
    return this._allowedShips;
  }

  get gameboardDimensions(): number {
    return this._gameboardDimensions;
  }

  get gamecellPixelSize(): number {
    return this._gamecellPixelSize;
  }

  set allowedShips(allowedShips: number)  {
    this._allowedShips = allowedShips;
  }

  set gameboardDimensions(gameboardDimensions: number){
    this._gameboardDimensions = gameboardDimensions;
  }

  set gamecellPixelSize(gamecellPixelSize: number){
    this._gamecellPixelSize = gamecellPixelSize;
  }
}
