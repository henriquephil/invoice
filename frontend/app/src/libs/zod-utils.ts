import { z } from 'zod';
import { LocalDate, ZonedDateTime } from '@js-joda/core';

export const zOffsetDateTime = z.preprocess((arg) => {
  if (typeof arg === "string") {
    return ZonedDateTime.parse(arg)
  }
  return arg;
}, z.custom<ZonedDateTime>((val) => val instanceof ZonedDateTime));

export const zLocalDate = z.preprocess((arg) => {
  if (typeof arg === "string") {
    return LocalDate.parse(arg)
  }
  return arg;
}, z.custom<LocalDate>((val) => val instanceof LocalDate));

export const zBigDecimal = z.preprocess((val) => {
  if (typeof val === "string") {
    const parsed = parseFloat(val);
    return isNaN(parsed) ? undefined : parsed;
  }
  return val;
}, z.number());

export const zBigDecimalOut = z.number().transform((val) => val.toString());
