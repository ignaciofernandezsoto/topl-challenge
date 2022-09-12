package com.ijfs.toplchallenge

import cats.data.ValidatedNel
import com.ijfs.toplchallenge.service.error.ToplException

package object validation:
  type ValidationResult[A] = ValidatedNel[ToplException, A]
