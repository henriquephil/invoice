export function getChangedValues<T extends Record<string, any>>(
  initialValues: T,
  currentValues: T
): Partial<T> {
  const changes: Partial<T> = {};
  for (const key in currentValues) {
    if (Object.prototype.hasOwnProperty.call(initialValues, key) && initialValues[key] !== currentValues[key]) {
      changes[key] = currentValues[key];
    }
  }
  return changes;
}
