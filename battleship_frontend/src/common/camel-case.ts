export class CamelCaseValueConverter {
  toView(value: string) {
    return value.charAt(0).toUpperCase() + value.substring(1);
  }
}
