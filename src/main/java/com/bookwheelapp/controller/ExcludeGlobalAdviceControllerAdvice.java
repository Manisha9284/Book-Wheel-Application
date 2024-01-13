package com.bookwheelapp.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice(assignableTypes = {ReaderController.class})
public class ExcludeGlobalAdviceControllerAdvice {

}
